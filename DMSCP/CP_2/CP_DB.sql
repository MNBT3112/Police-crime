-- Drop database if it exists
DROP DATABASE IF EXISTS policedb;
CREATE DATABASE policedb;
USE policedb;

-- Creating tables
CREATE TABLE police_stations (
    station_id INT AUTO_INCREMENT PRIMARY KEY,
    station_name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL
);

CREATE TABLE officers (
    officer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    rank VARCHAR(50),
    station_id INT,
    FOREIGN KEY (station_id) REFERENCES police_stations(station_id) ON DELETE CASCADE
);

CREATE TABLE citizens (
    citizen_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    dob DATE
);

CREATE TABLE cases (
    case_id INT AUTO_INCREMENT PRIMARY KEY,
    fir_id INT NOT NULL,
    officer_id INT,
    case_type VARCHAR(255) NOT NULL,
    status ENUM('Pending', 'Under Investigation', 'Closed') NOT NULL,
    FOREIGN KEY (officer_id) REFERENCES officers(officer_id) ON DELETE SET NULL
);

CREATE TABLE firs (
    fir_id INT AUTO_INCREMENT PRIMARY KEY,
    citizen_id INT,
    description TEXT,
    filing_date DATE,
    FOREIGN KEY (citizen_id) REFERENCES citizens(citizen_id) ON DELETE CASCADE
);

CREATE TABLE evidence (
    evidence_id INT AUTO_INCREMENT PRIMARY KEY,
    case_id INT,
    description TEXT,
    evidence_type ENUM('Physical', 'Document', 'Video', 'Other') NOT NULL,
    FOREIGN KEY (case_id) REFERENCES cases(case_id) ON DELETE CASCADE
);

CREATE TABLE arrests (
    arrest_id INT AUTO_INCREMENT PRIMARY KEY,
    officer_id INT,
    citizen_id INT,
    case_id INT,
    arrest_date DATE,
    FOREIGN KEY (officer_id) REFERENCES officers(officer_id) ON DELETE CASCADE,
    FOREIGN KEY (citizen_id) REFERENCES citizens(citizen_id) ON DELETE CASCADE,
    FOREIGN KEY (case_id) REFERENCES cases(case_id) ON DELETE CASCADE
);

CREATE TABLE criminal_records (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    citizen_id INT,
    crime_description TEXT,
    date_of_crime DATE,
    FOREIGN KEY (citizen_id) REFERENCES citizens(citizen_id) ON DELETE CASCADE
);

CREATE TABLE incidents (
    incident_id INT AUTO_INCREMENT PRIMARY KEY,
    location VARCHAR(255),
    description TEXT,
    incident_date DATE
);

CREATE TABLE crimes (
    crime_id INT AUTO_INCREMENT PRIMARY KEY,
    crime_type VARCHAR(100) NOT NULL,
    location VARCHAR(200) NOT NULL,
    date_reported DATE,
    description TEXT,
    status VARCHAR(50) DEFAULT 'Open'
);

-- Inserting sample data for police_stations
INSERT INTO police_stations (station_name, location) VALUES
('Pune City Police Station', 'Pune, Maharashtra'),
('Kothrud Police Station', 'Pune, Maharashtra'),
('Hinjawadi Police Station', 'Pune, Maharashtra'),
('Shivajinagar Police Station', 'Pune, Maharashtra'),
('Bavdhan Police Station', 'Pune, Maharashtra'),
('Viman Nagar Police Station', 'Pune, Maharashtra'),
('Pimpri Police Station', 'Pune, Maharashtra'),
('Khadki Police Station', 'Pune, Maharashtra'),
('Wakad Police Station', 'Pune, Maharashtra'),
('Hadapsar Police Station', 'Pune, Maharashtra');

-- Inserting sample data for officers
INSERT INTO officers (name, rank, station_id) VALUES
('John Doe', 'Inspector', 1),
('Jane Smith', 'Sergeant', 2),
('Rajesh Patil', 'Constable', 3),
('Arvind Deshmukh', 'Sub Inspector', 4),
('Suresh Shinde', 'Inspector', 5),
('Anjali Patil', 'Sergeant', 6),
('Vishal Desai', 'Constable', 7),
('Pooja Sharma', 'Sub Inspector', 8),
('Amit Patil', 'Sergeant', 9),
('Ravi Yadav', 'Constable', 10);

-- Inserting sample data for citizens
INSERT INTO citizens (name, address, dob) VALUES
('Alice Johnson', '123 Main St, Pune', '1990-05-12'),
('Bob Williams', '456 Oak St, Pune', '1985-09-23'),
('Charlie Brown', '789 Pine St, Pune', '1992-02-15'),
('David White', '101 Maple St, Pune', '1980-08-10'),
('Eve Green', '202 Birch St, Pune', '1987-12-05'),
('Frank Black', '303 Cedar St, Pune', '1993-03-20'),
('Grace Blue', '404 Elm St, Pune', '1988-07-25'),
('Hank Grey', '505 Willow St, Pune', '1991-11-30'),
('Ivy Violet', '606 Oakwood St, Pune', '1994-01-18'),
('Jack Orange', '707 Rose St, Pune', '1986-04-05');

-- Insert more than 100 entries into firs (for simplicity, here are a few examples)
INSERT INTO firs (citizen_id, description, filing_date) VALUES
(1, 'Reported a stolen bicycle', '2025-02-10'),
(2, 'Vehicle theft in parking lot', '2025-02-12'),
(3, 'Accident on main road', '2025-02-14'),
(4, 'Lost wallet at market', '2025-02-16'),
(5, 'Theft from house', '2025-02-17'),
(6, 'Assault in park', '2025-02-20'),
(7, 'Vandalism in public place', '2025-02-22'),
(8, 'Reported a fight at nightclub', '2025-02-23'),
(9, 'Shoplifting incident at mall', '2025-02-25'),
(10, 'Road rage incident on highway', '2025-02-27');

-- Add more records as required to make up to 100 FIRs

-- Insert 100 sample cases
INSERT INTO cases (fir_id, officer_id, case_type, status) VALUES
(1, 1, 'Theft', 'Under Investigation'),
(2, 2, 'Robbery', 'Pending'),
(3, 3, 'Accident', 'Closed'),
(4, 4, 'Theft', 'Pending'),
(5, 5, 'Vandalism', 'Under Investigation'),
(6, 6, 'Assault', 'Closed'),
(7, 7, 'Burglary', 'Under Investigation'),
(8, 8, 'Fraud', 'Closed'),
(9, 9, 'Theft', 'Pending'),
(10, 10, 'Shoplifting', 'Under Investigation');

-- Insert 100 sample evidence records
INSERT INTO evidence (case_id, description, evidence_type) VALUES
(1, 'Security footage', 'Video'),
(2, 'Fingerprints on stolen items', 'Physical'),
(3, 'Witness statement', 'Document'),
(4, 'CCTV footage from the parking lot', 'Video'),
(5, 'Broken lock', 'Physical'),
(6, 'Victim’s medical report', 'Document'),
(7, 'Surveillance photos', 'Physical'),
(8, 'Bank transaction records', 'Document'),
(9, 'Store security footage', 'Video'),
(10, 'Clothing worn by suspect', 'Physical');

-- Insert 100 sample arrests
INSERT INTO arrests (officer_id, citizen_id, case_id, arrest_date) VALUES
(1, 1, 1, '2025-03-01'),
(2, 2, 2, '2025-03-02'),
(3, 3, 3, '2025-03-05'),
(4, 4, 4, '2025-03-06'),
(5, 5, 5, '2025-03-08'),
(6, 6, 6, '2025-03-10'),
(7, 7, 7, '2025-03-12'),
(8, 8, 8, '2025-03-14'),
(9, 9, 9, '2025-03-15'),
(10, 10, 10, '2025-03-16');

-- Insert 100 sample criminal records
INSERT INTO criminal_records (citizen_id, crime_description, date_of_crime) VALUES
(1, 'Theft of bicycle', '2025-02-10'),
(2, 'Vehicle theft', '2025-02-12'),
(3, 'Accident on main road', '2025-02-14'),
(4, 'Lost wallet', '2025-02-16'),
(5, 'House burglary', '2025-02-17'),
(6, 'Assault at park', '2025-02-20'),
(7, 'Vandalism', '2025-02-22'),
(8, 'Fight at nightclub', '2025-02-23'),
(9, 'Shoplifting', '2025-02-25'),
(10, 'Road rage incident', '2025-02-27');

-- Insert 100 sample incidents
INSERT INTO incidents (location, description, incident_date) VALUES
('Pune, Maharashtra', 'Theft at a local store', '2025-03-15'),
('Pune, Maharashtra', 'Robbery at ATM', '2025-03-17'),
('Pune, Maharashtra', 'Assault at a pub', '2025-03-18'),
('Pune, Maharashtra', 'Vandalism at public park', '2025-03-20'),
('Pune, Maharashtra', 'Traffic accident on highway', '2025-03-22'),
('Pune, Maharashtra', 'Shoplifting at mall', '2025-03-24'),
('Pune, Maharashtra', 'Battery at night', '2025-03-26'),
('Pune, Maharashtra', 'Fraud at bank', '2025-03-28'),
('Pune, Maharashtra', 'Breaking into car', '2025-03-30'),
('Pune, Maharashtra', 'Theft at shopping center', '2025-04-01');

-- Insert 100 sample crimes
INSERT INTO crimes (crime_type, location, date_reported, description, status) VALUES
('Theft', 'Pune, Maharashtra', '2025-03-15', 'Bicycle stolen from outside shopping mall', 'Open'),
('Burglary', 'Pune, Maharashtra', '2025-03-10', 'Home break-in, electronics stolen', 'Open'),
('Assault', 'Pune, Maharashtra', '2025-03-12', 'Physical altercation outside nightclub', 'Open'),
('Theft', 'Pune, Maharashtra', '2025-03-14', 'Purse snatching incident', 'Open'),
('Vandalism', 'Pune, Maharashtra', '2025-03-16', 'Graffiti on public building', 'Open'),
('Theft', 'Pune, Maharashtra', '2025-03-18', 'Shoplifting at convenience store', 'Open'),
('Drug Offense', 'Pune, Maharashtra', '2025-03-17', 'Possession of illegal substances', 'Open');


select * from crimes;

CREATE OR REPLACE VIEW PublicData AS
SELECT 
    ps.station_name,
    ps.location,
    c.case_id,
    c.case_type
FROM cases c
JOIN officers o ON c.officer_id = o.officer_id
JOIN police_stations ps ON o.station_id = ps.station_id;


CREATE OR REPLACE VIEW OfficerData AS
SELECT 
    c.case_id,
    c.case_type,
    o.officer_id,
    o.name AS officer_name,
    ps.station_id,
    ps.station_name,
    ps.location,
    f.fir_id,
    f.filing_date,
    ct.citizen_id,
    ct.name AS citizen_name,
    ct.address,
    cr.record_id,
    cr.crime_description,
    e.evidence_id,
    e.description AS evidence_description,
    e.evidence_type,
    a.arrest_id,
    a.arrest_date,
    inc.incident_id,
    inc.description AS incident_description,
    inc.incident_date
FROM cases c
LEFT JOIN officers o ON c.officer_id = o.officer_id
LEFT JOIN police_stations ps ON o.station_id = ps.station_id
LEFT JOIN firs f ON c.fir_id = f.fir_id
LEFT JOIN citizens ct ON f.citizen_id = ct.citizen_id
LEFT JOIN criminal_records cr ON ct.citizen_id = cr.citizen_id
LEFT JOIN evidence e ON c.case_id = e.case_id
LEFT JOIN arrests a ON c.case_id = a.case_id
LEFT JOIN incidents inc ON ps.location = inc.location;

select * from PublicData;
select * from OfficerData;
