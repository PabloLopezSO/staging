/*
Sequences for Id's. Does not follow SWO's naming conventions!
20220407(FML): Still don't know if it is needed for MySQL
*/
CREATE SEQUENCE HIBERNATE_SEQUENCE AS INT;

/*
This table holds the task status
*/
CREATE TABLE taskStatuses(id INT PRIMARY KEY,
    description VARCHAR(255)  NOT NULL);

CREATE TABLE users(
    id INT PRIMARY KEY AUTO_INCREMENT,
    mail VARCHAR(255) NOT NULL UNIQUE);


/*
This table holds the task details
Some details have been intentionally left behind:
1.- large text objects are defined differently per RDBMS. Read the f****** manual!
2.- TimeZones a VERY important and, also, defined differently per RDBMS
3.- Think about audit fields.
4.- Self-generating IDs
*/
CREATE TABLE tasks(id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) DEFAULT 'Untitled' NOT NULL UNIQUE,
    description VARCHAR(2000),
    status INT NOT NULL,
    dueDate DATETIME NOT NULL,
    createdDate DATETIME NOT NULL,
    creator INTEGER NOT NULL,
    assignee INTEGER,
    file1 VARCHAR(255),
    file1UploadDate DATETIME,
    azureFile1 VARCHAR(255),
    downloadFile1 VARCHAR(255),
    thumbnailFile1 VARCHAR(255),
    file2 VARCHAR(255),
    file2UploadDate DATETIME,
    azureFile2 VARCHAR(255),
    downloadFile2 VARCHAR(255),
    thumbnailFile2 VARCHAR(255),
    file3 VARCHAR(255),
    file3UploadDate DATETIME,
    azureFile3 VARCHAR(255),
    downloadFile3 VARCHAR(255),
    thumbnailFile3 VARCHAR(255),
    CONSTRAINT FK_TASKS_CREATOR FOREIGN KEY (creator) REFERENCES users(id),
    CONSTRAINT FK_TASKS_STATUS FOREIGN KEY (status) REFERENCES taskStatuses(id)
    );

CREATE TABLE progress(
    id INT PRIMARY KEY AUTO_INCREMENT,
    newStatus VARCHAR(255) NOT NULL,
    modifiedDate DATETIME NOT NULL,
    taskId INT NOT NULL,
    userId INT NOT NULL,
    CONSTRAINT FK_PROGRESS_TASKID FOREIGN KEY (taskId) REFERENCES tasks(id),
    CONSTRAINT FK_PROGRESS_USERID FOREIGN KEY (userId) REFERENCES users(id)
    );
