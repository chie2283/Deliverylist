--DROP TABLE IF EXISTS destination;
--DROP TABLE IF EXISTS patient;
--DROP TABLE IF EXISTS enteralNutrient;
--DROP TABLE IF EXISTS prescription;

CREATE TABLE IF NOT EXISTS destination (
    id INT DEFAULT 0 NOT NULL AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS patient (
    id INT DEFAULT 0 NOT NULL AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    destination_id INT,
    PRIMARY KEY (id),
    FOREIGN KEY (destination_id) REFERENCES destination(id)
);

CREATE TABLE IF NOT EXISTS enteralNutrient (
    id INT DEFAULT 0 NOT NULL AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS prescription (
    id INT DEFAULT 0 NOT NULL AUTO_INCREMENT,
    patient_id INT,
    enteralNutrient_id INT,
    dosage VARCHAR(128),
    dt DATE,
    days INT,
    deliveryDt DATE,
    doneDays INT,
    done BOOLEAN,
    PRIMARY KEY (id),
    FOREIGN KEY (patient_id) REFERENCES patient(id),
    FOREIGN KEY (enteralNutrient_id) REFERENCES enteralNutrient(id)
);