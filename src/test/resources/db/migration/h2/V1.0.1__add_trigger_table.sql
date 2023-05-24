CREATE TABLE public.trigger (
    id bigint NOT NULL,
    organization_id uuid NOT NULL,
    name text NOT NULL,
    description text,
    conf text NOT NULL,
    last_status character varying(255) NOT NULL,
    priority character varying(255) NOT NULL,
    last_status_update timestamp NOT NULL,
    enabled boolean,
    suppressed_score int,
    muted boolean
);

CREATE SEQUENCE public.trigger_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.trigger
    ADD CONSTRAINT trigger_pkey PRIMARY KEY (id);
