/*
Task instance
*/
INSERT INTO tasks (id,title,description,status,dueDate,createdDate) 
VALUES (2,'My First Task!' ,'This task is make your career start!' , 1,'2022-04-25','2022-04-20',1);

INSERT INTO users (id, email) 
VALUES (1,'none@mail.com');

INSERT INTO progress (id, newStatus, modifiedDate, taskId, userId) 
VALUES (1,'Created','2022-04-25', 2, 1 );