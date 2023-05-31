CREATE TABLE public.alert (
    id bigint PRIMARY KEY NOT NULL,
    alert_timestamp timestamp without time zone,
    operation_data character varying(255),
    trigger_id bigint NOT NULL,
    trigger_status character varying(255) DEFAULT 'UNCHECKED',
    organization_id uuid NOT NULL
);
CREATE SEQUENCE public.alert_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE alert ADD CONSTRAINT fktkd1v536csk4aheewsoivyiks FOREIGN KEY (trigger_id) REFERENCES trigger(id);
ALTER TABLE alert ADD CONSTRAINT fdtkd1v536csk4aheewsoivyips FOREIGN KEY (organization_id) REFERENCES organization(id);
ALTER TABLE trigger ADD CONSTRAINT fdtkd1v536csk4ateewsoivyits FOREIGN KEY (organization_id) REFERENCES organization(id);
