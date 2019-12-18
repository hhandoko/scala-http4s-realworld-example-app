-- Truncate
-- ~~~~~~
TRUNCATE TABLE auth RESTART IDENTITY CASCADE;
TRUNCATE TABLE profile RESTART IDENTITY CASCADE;


-- Insert seed data
-- ~~~~~~
INSERT INTO profile (username, email)
     VALUES ('john.doe', 'john.doe@test.com')
          , ('jane.doe', 'jane.doe@test.com')
          ;

INSERT INTO auth (profile_id, password)
     SELECT id, 'Secret!'
       FROM profile
      WHERE lower(profile.email) = lower('john.doe@test.com');

INSERT INTO auth (profile_id, password)
     SELECT id, 'Secret!'
       FROM profile
      WHERE lower(profile.email) = lower('jane.doe@test.com');
