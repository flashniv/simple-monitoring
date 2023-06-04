CREATE TABLE public.alerter (
    id bigint PRIMARY KEY NOT NULL,
    min_priority character varying(255) NOT NULL,
    description character varying(255),
    class_name character varying(255) NOT NULL,
    properties character varying(255) NOT NULL,
    organization_id uuid NOT NULL
);
CREATE SEQUENCE public.alerter_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE alerter ADD CONSTRAINT alerter_organization_fk FOREIGN KEY (organization_id) REFERENCES organization(id);
