CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE conference (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    creation_date DATETIME,
    state VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE paper (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    abstract_text TEXT,
    creation_date DATETIME,
    content TEXT,
    state VARCHAR(255),
    conference_id BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE review (
    id BIGINT NOT NULL AUTO_INCREMENT,
    paper_id BIGINT NOT NULL,
    reviewer_id BIGINT NOT NULL,
    comments TEXT,
    rating INT,
    PRIMARY KEY (id)
);

CREATE TABLE paper_authors (
    paper_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (paper_id, user_id)
);

CREATE TABLE paper_reviewers (
    paper_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (paper_id, user_id)
);

CREATE TABLE conference_pc_members (
    conference_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (conference_id, user_id)
);

CREATE TABLE conference_pc_chairs (
    conference_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (conference_id, user_id)
);

CREATE TABLE user_roles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO users (username, password) VALUES
('visitor', '$2a$10$LjldP4SvYH8Hl7XIgBfGruT25Q9u0WiNMlkVtvqi6yAC3SZkGNeVO'),
('pc-chair', '$2a$10$ATeR8lS1JqI.n.grZujv1.QZ7rlg44xP5rh6kg.n6BG1l93BL1Cbi'),
('author', '$2a$10$PaFmivE6TSQujYBVFw5rr.KFEBCSbo7hvRGKiKWIOi7h8yDznJiZe'),
('reviewer', '$2a$10$K8ksIVtUGd5g.qrEjlv5vO0ycrOK1Ugrqxt2ieeI0SuxNfOYc/WHG');

INSERT INTO user_roles (user_id, role) VALUES
(1, 'AUTHOR'),
(2, 'PC_CHAIR'),
(3, 'AUTHOR'),
(4, 'PC_MEMBER');

INSERT INTO conference (title, description, creation_date, state) VALUES
('Data Conference 2024', 'A conference on Data technologies.', '2024-09-01 10:00:00', 'SUBMISSION'),
('Tech Conference 2024', 'A conference on the latest in tech.', '2024-09-05 12:00:00', 'REVIEW');


INSERT INTO paper (title, abstract_text, creation_date, content, state, conference_id) VALUES
('Data Paper 1', 'Abstract of Data Paper 1', '2024-09-10 14:00:00', 'Content of Data Paper 1', 'SUBMITTED', 1),
('Tech Paper 1', 'Abstract of Tech Paper 1', '2024-09-12 16:00:00', 'Content of Tech Paper 1', 'REVIEW', 2);

INSERT INTO review (paper_id, reviewer_id, comments, rating) VALUES
(1, 4, 'Good paper on Data.', 8),
(2, 4, 'Needs some improvments.', 6);

INSERT INTO paper_authors (paper_id, user_id) VALUES
(1, 1),
(1, 3),
(2, 3);

INSERT INTO paper_reviewers (paper_id, user_id) VALUES
(1, 4),
(2, 4);

INSERT INTO conference_pc_members (conference_id, user_id) VALUES
(1, 4),
(2, 4);

INSERT INTO conference_pc_chairs (conference_id, user_id) VALUES
(1, 2),
(2, 2);