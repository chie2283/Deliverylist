DROP TABLE IF EXISTS prescription;
DROP TABLE IF EXISTS enteral_nutrient;
DROP TABLE IF EXISTS patient;
DROP TABLE IF EXISTS destination;

CREATE TABLE IF NOT EXISTS destination (
    id INT DEFAULT 0 NOT NULL AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS patient (
    id INT DEFAULT 0 NOT NULL AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    birthday DATE NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS enteral_nutrient (
    id INT DEFAULT 0 NOT NULL AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS prescription (
    id INT DEFAULT 0 NOT NULL AUTO_INCREMENT,
    destination_id INT NOT NULL,
    patient_id INT NOT NULL,
    enteral_nutrient_id INT NOT NULL,
    dosage VARCHAR(128) NOT NULL,
    dt DATE NOT NULL,
    days INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    delivery_dt DATE NOT NULL,
    done_days INT NOT NULL,
    done BOOLEAN,
    PRIMARY KEY (id),
    FOREIGN KEY (destination_id) REFERENCES destination(id),
    FOREIGN KEY (patient_id) REFERENCES patient(id),
    FOREIGN KEY (enteral_nutrient_id) REFERENCES enteral_nutrient(id)
);