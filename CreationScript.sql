CREATE DATABASE IF NOT EXISTS NexaBankDB;

CREATE SCHEMA loanschema;

CREATE EXTENSION IF NOT EXISTS pgcrypto;  
CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE IF NOT EXISTS loanschema.users (
  user_id         uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  name            varchar(100) NOT NULL,
  dob             date NOT NULL,
  gender          varchar(10),
  email           varchar(100) NOT NULL UNIQUE,
  phone           varchar(15) UNIQUE,
  annual_salary   numeric(12,2) NOT NULL CHECK (annual_salary >= 25000),
  credit_score    int NOT NULL CHECK (credit_score BETWEEN 300 AND 850),
  created_at      timestamptz NOT NULL DEFAULT now()
);

-- Loan Types
CREATE TABLE IF NOT EXISTS loanschema.loan_types (
  loan_type_id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  type         text NOT NULL UNIQUE,                 -- e.g., 'mortgage','auto loan'
  description  text,
  status       text NOT NULL DEFAULT 'active' CHECK (status IN ('active','inactive'))
);

-- Loan Points 
CREATE TABLE IF NOT EXISTS loanschema.loan_points (
  loan_point_id                  uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  loan_type_id                   uuid NOT NULL REFERENCES loanschema.loan_types(loan_type_id) ON DELETE CASCADE,
  max_loan_amount                numeric(12,2) CHECK (max_loan_amount >= 0),
  percentage_on_loan_approval    numeric(12,2) NOT NULL CHECK (percentage_on_loan_approval >= 0), -- percent, e.g. 25.00
  min_credit_score               int NOT NULL,
  max_credit_score               int NOT NULL,
  CHECK (min_credit_score >= 500 AND max_credit_score <= 850 AND min_credit_score <= max_credit_score)
);
CREATE INDEX IF NOT EXISTS idx_loan_points_lookup
  ON loanschema.loan_points (loan_type_id, min_credit_score, max_credit_score);


-- Eligibility Rules (kept close to your version)
CREATE TABLE IF NOT EXISTS loanschema.loan_eligibility_rules (
  loan_eid          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  loan_type_id      uuid NOT NULL REFERENCES loanschema.loan_types(loan_type_id) ON DELETE CASCADE,
  max_loan_amount   numeric(12,2) CHECK (max_loan_amount IS NULL OR max_loan_amount >= 0),
  apr               numeric(5,2)  CHECK (apr IS NULL OR (apr >= 0 AND apr <= 36)),
  min_salary        numeric(12,2) NOT NULL CHECK (min_salary >= 2000),
  min_credit_score  int NOT NULL CHECK (min_credit_score BETWEEN 500 AND 850),
  max_credit_score  int CHECK (max_credit_score IS NULL OR (max_credit_score BETWEEN 500 AND 850 AND max_credit_score >= min_credit_score)),
  loan_point_id     uuid REFERENCES loanschema.loan_points(loan_point_id)
);

-- Loan Application (driver table)
CREATE TABLE IF NOT EXISTS loanschema.loan_application (
  loan_id           uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id           uuid NOT NULL REFERENCES loanschema.users(user_id) ON DELETE CASCADE,
  loan_type_id      uuid NOT NULL REFERENCES loanschema.loan_types(loan_type_id),
  loan_eid          uuid REFERENCES loanschema.loan_eligibility_rules(loan_eid),
  requested_amount  numeric(12,2) NOT NULL CHECK (requested_amount >= 0),
  submitted_date    date,
  sanction_amount   numeric(12,2) CHECK (sanction_amount IS NULL OR sanction_amount >= 0),
  status            text NOT NULL CHECK (
                     status IN ('Draft','Submitted','Inprogress','Processing','Approved','Rejected')
                   ),
  created_at        timestamptz NOT NULL DEFAULT now(),
  approved_at       timestamptz
);
 
-- Guard: one in-flight app per (user, loan type)
CREATE UNIQUE INDEX IF NOT EXISTS uq_open_app_per_user_type
ON loanschema.loan_application (user_id, loan_type_id)
WHERE status IN ('Draft','Submitted','Inprogress','Processing');

---auidt_logs
CREATE TABLE loanschema.loan_audit_logs (
  audit_id    uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  loan_id     uuid NOT NULL REFERENCES loanschema.loan_application(loan_id) ON DELETE CASCADE,
  user_id     uuid NOT NULL REFERENCES loanschema.users(user_id),
  status      text NOT NULL CHECK (
                status IN (
                  'Draft','Submitted','Inprogress','Processing',
                  'Approved','Rejected','Accepted','Denied'
                )
              ),
  updated_by  varchar(100),
  created_at  timestamptz NOT NULL DEFAULT now(),
  updated_at  timestamptz
);
 
-- Index for quick lookups by loan
CREATE INDEX idx_audit_loan_created
  ON loanschema.loan_audit_logs (loan_id, created_at);

-- Offer / repayment option snapshot
CREATE TABLE IF NOT EXISTS loanschema.approved_loans_repayment_options (
  repayment_id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  loan_id                 uuid NOT NULL UNIQUE REFERENCES loanschema.loan_application(loan_id) ON DELETE CASCADE,
  months                  int  NOT NULL CHECK (months > 0),
  apr                     numeric(5,2)  NOT NULL CHECK (apr >= 0 AND apr <= 36),
  monthly_payments        numeric(12,2) NOT NULL CHECK (monthly_payments >= 0),
  total_principal_amount  numeric(12,2) NOT NULL CHECK (total_principal_amount >= 0),
  total_payable_amount    numeric(12,2) NOT NULL CHECK (total_payable_amount >= 0),
  updated_at              timestamptz NOT NULL DEFAULT now()
);



-- Trigger 1: Audit inserts & status changes

CREATE OR REPLACE FUNCTION loanschema.audit_loan_status_change()
RETURNS trigger
LANGUAGE plpgsql
AS $$
DECLARE
  v_actor_user_id uuid := NULL;
BEGIN
  BEGIN
    v_actor_user_id := current_setting('app.actor_user_id', true)::uuid;
  EXCEPTION WHEN others THEN
    v_actor_user_id := NULL;
  END;
  IF TG_OP = 'INSERT' THEN
    INSERT INTO loanschema.loan_audit_logs (
      audit_id, loan_id, user_id, status,
      from_status, to_status, actor_user_id,
      updated_by, reason_code, notes, created_at, updated_at
    )
    VALUES (
      gen_random_uuid(),
      NEW.loan_id,
      NEW.user_id,
      NEW.status,
      NULL,
      NEW.status,
      v_actor_user_id,
      current_user,
      NULL,
      NULL,
      now(),
      now()
    );
    RETURN NEW;
  ELSIF TG_OP = 'UPDATE' THEN
    IF NEW.status IS DISTINCT FROM OLD.status THEN
      INSERT INTO loanschema.loan_audit_logs (
        audit_id, loan_id, user_id, status,
        from_status, to_status, actor_user_id,
        updated_by, reason_code, notes, created_at, updated_at
      )
      VALUES (
        gen_random_uuid(),
        NEW.loan_id,
        NEW.user_id,
        NEW.status,
        OLD.status,
        NEW.status,
        v_actor_user_id,
        current_user,
        NULL,
        NULL,
        now(),
        now()
      );
    END IF;
    RETURN NEW;
  END IF;
  RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_audit_loan_insert ON loanschema.loan_application;
CREATE TRIGGER trg_audit_loan_insert
AFTER INSERT ON loanschema.loan_application
FOR EACH ROW
EXECUTE FUNCTION loanschema.audit_loan_status_change();

DROP TRIGGER IF EXISTS trg_audit_loan_status ON loanschema.loan_application;
CREATE TRIGGER trg_audit_loan_status
AFTER UPDATE OF status ON loanschema.loan_application
FOR EACH ROW
EXECUTE FUNCTION loanschema.audit_loan_status_change();

-- Trigger 2: Validate allowed status transitions

CREATE OR REPLACE FUNCTION loanschema.validate_loan_status_transition()
RETURNS trigger
LANGUAGE plpgsql
AS $$
DECLARE
  allowed_next text[];
BEGIN
  IF TG_OP = 'UPDATE' AND NEW.status IS DISTINCT FROM OLD.status THEN
    CASE OLD.status
      WHEN 'Draft'      THEN allowed_next := ARRAY['Submitted'];
      WHEN 'Submitted'  THEN allowed_next := ARRAY['Inprogress'];
      WHEN 'Inprogress' THEN allowed_next := ARRAY['Processing'];
      WHEN 'Processing' THEN allowed_next := ARRAY['Approved','Rejected'];
      WHEN 'Approved'   THEN allowed_next := ARRAY[]::text[];
      WHEN 'Rejected'   THEN allowed_next := ARRAY[]::text[];
      ELSE
        RAISE EXCEPTION 'Unknown current status: %', OLD.status;
    END CASE;
    IF array_position(allowed_next, NEW.status) IS NULL THEN
      RAISE EXCEPTION
        'Illegal status transition: % -> % (allowed: %)',
        OLD.status, NEW.status, allowed_next;
    END IF;
  END IF;
  RETURN NEW;
END;
$$;
DROP TRIGGER IF EXISTS trg_validate_status ON loanschema.loan_application;
CREATE TRIGGER trg_validate_status
BEFORE UPDATE OF status ON loanschema.loan_application
FOR EACH ROW
EXECUTE FUNCTION loanschema.validate_loan_status_transition();