CREATE TABLE public._user (
    id uuid NOT NULL,
    email character varying(255) NOT NULL,
    firstname character varying(255),
    lastname character varying(255),
    password character varying(255),
    role character varying(255)
);

CREATE TABLE public.access_token (
    id uuid NOT NULL,
    organization_id uuid NOT NULL
);

CREATE TABLE public.metric (
    id bigint NOT NULL,
    name text NOT NULL,
    organization_id uuid NOT NULL
);

CREATE SEQUENCE public.metric_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE public.organization (
    id uuid NOT NULL,
    name character varying(255) NOT NULL
);

CREATE TABLE public.organization_user (
    organization_id uuid NOT NULL,
    user_id uuid NOT NULL
);

CREATE TABLE public.parameter_group (
    id bigint NOT NULL,
    parameters text,
    metric_id bigint NOT NULL
);

CREATE SEQUENCE public.parameter_group_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE public.token (
    id bigint NOT NULL,
    expired boolean NOT NULL,
    revoked boolean NOT NULL,
    token character varying(255),
    user_id uuid
);

CREATE SEQUENCE public.token_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE ONLY public._user
    ADD CONSTRAINT _user_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public._user
    ADD CONSTRAINT uk_pdabhgwxnms2aceeku9s2ewy5 UNIQUE (email);

ALTER TABLE ONLY public.access_token
    ADD CONSTRAINT access_token_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.metric
    ADD CONSTRAINT metric_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.metric
    ADD CONSTRAINT uk_bcabhgwxnms2aceeku9s2ewy5 UNIQUE (name,organization_id);

ALTER TABLE ONLY public.organization
    ADD CONSTRAINT organization_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.parameter_group
    ADD CONSTRAINT parameter_group_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.token
    ADD CONSTRAINT token_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.token
    ADD CONSTRAINT uk_pddrhgwxnms2aceeku9s2ewy5 UNIQUE (token);

ALTER TABLE ONLY public.parameter_group
    ADD CONSTRAINT fk8y5oxt4im8gb2wh00jb24x68i FOREIGN KEY (metric_id) REFERENCES public.metric(id);

ALTER TABLE ONLY public.access_token
    ADD CONSTRAINT fke3nr093qmgefxaibpx6hic4t8 FOREIGN KEY (organization_id) REFERENCES public.organization(id);

ALTER TABLE ONLY public.metric
    ADD CONSTRAINT fkgickq0i3tkflxwtne0k5kfnax FOREIGN KEY (organization_id) REFERENCES public.organization(id);

ALTER TABLE ONLY public.token
    ADD CONSTRAINT fkiblu4cjwvyntq3ugo31klp1c6 FOREIGN KEY (user_id) REFERENCES public._user(id);

ALTER TABLE ONLY public.organization_user
    ADD CONSTRAINT fkm8950wa9al3qxgx1b6v16v9bn FOREIGN KEY (organization_id) REFERENCES public.organization(id);

ALTER TABLE ONLY public.organization_user
    ADD CONSTRAINT fkpthp0cnc7bx427bjmtiv1wjlj FOREIGN KEY (user_id) REFERENCES public._user(id);
