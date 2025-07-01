create table users (
  id UUID NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
  first_name STRING NOT NULL,
  last_name STRING NOT NULL,
  username STRING NOT NULL UNIQUE,
  created_at TIMESTAMP NOT NULL default now(),
  updated_at TIMESTAMP NOT NULL default now()
);