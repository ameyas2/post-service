CREATE TABLE public.post (
        id UUID NOT NULL DEFAULT gen_random_uuid(),
        title STRING NOT NULL,
        description STRING NOT NULL,
        created_at TIMESTAMP NOT NULL DEFAULT now():::TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT now():::TIMESTAMP,
        user_id UUID NULL,
        CONSTRAINT post_pkey PRIMARY KEY (id ASC),
        CONSTRAINT fk7ky67sgi7k0ayf22652f7763r FOREIGN KEY (user_id) REFERENCES public.users(id)
)


create table post (
  id UUID NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
  title STRING NOT NULL,
  description STRING NOT NULL,
  created_at TIMESTAMP NOT NULL default now(),
  updated_at TIMESTAMP NOT NULL default now(),
  FOREIGN KEY (user_id) REFERENCES public.users(id)
);