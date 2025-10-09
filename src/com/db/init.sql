CREATE TABLE person (
                        id              BIGSERIAL PRIMARY KEY,
                        public_id       UUID UNIQUE NOT NULL,
                        first_name      VARCHAR(50) NOT NULL,
                        last_name       VARCHAR(50) NOT NULL,
                        dob             DATE NOT NULL,

    -- employee-specific fields
                        employee_type   VARCHAR(20) CHECK (
                            (person_kind <> 'EMPLOYEE' AND employee_type IS NULL)
                                OR (person_kind = 'EMPLOYEE' AND employee_type IN ('PISTER', 'LIFT_OP', 'RESTAURATION', 'MAINTENANCE'))
                            ),
    -- employee + instructors specific field
                        worksite    VARCHAR(50) CHECK (
                            (person_kind = 'GUEST' AND worksite IS NULL)
                                OR (person_kind <> 'GUEST' AND worksite IS NOT NULL)),

    -- instructor-specific field
                        ski_school  VARCHAR(50) CHECK (
                            (person_kind <> 'INSTRUCTOR' AND ski_school IS NULL)
                                OR (person_kind = 'INSTRUCTOR' AND ski_school IS NOT NULL)),

    -- replace java extends for kinds of subclass
                        person_kind     VARCHAR(20) NOT NULL CHECK (person_kind IN ('EMPLOYEE', 'GUEST', 'INSTRUCTOR')),

                        created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_person_kind ON person (person_kind);
CREATE INDEX idx_employee_type ON person (employee_type) WHERE person_kind = 'EMPLOYEE';

CREATE TABLE pass (
                      id              BIGSERIAL PRIMARY KEY,
                      public_id       UUID UNIQUE NOT NULL,
                      owner_id        BIGINT NOT NULL REFERENCES person(id) ON DELETE CASCADE,

                      valid_from      DATE NOT NULL,
                      valid_until     DATE NOT NULL,
                      price           NUMERIC(8,2) NOT NULL CHECK (price >= 0),

                      pass_type       VARCHAR(20) NOT NULL CHECK (pass_type IN ('DAY', 'MULTIDAY', 'ALACARTE', 'SEASON')),
                      pass_status     VARCHAR(20) NOT NULL CHECK (pass_status IN ('ACTIVE', 'SUSPENDED', 'EXPIRED')),

                      created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_pass_owner ON pass (owner_id);
CREATE INDEX idx_pass_type ON pass (pass_type);
CREATE INDEX idx_pass_validity ON pass (valid_from, valid_until);

ALTER TABLE pass
    ADD CONSTRAINT chk_pass_dates CHECK (valid_until >= valid_from);

CREATE TABLE pass_day_used (
                        pass_id    BIGINT NOT NULL REFERENCES pass(id) ON DELETE CASCADE,
                        used_date  DATE NOT NULL,
                        PRIMARY KEY (pass_id, used_date)
);

CREATE INDEX idx_pass_day_used_date ON pass_day_used (used_date);


