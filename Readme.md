# Todo App

### This app is a CRUD example where only getAll and insert actions are implemented.

You can see the swagger definition [here](http://localhost:8080/swagger-ui/index.html)

As part of your training you will be asked to make it grow implementing new features on top of it.

Hope you enjoy it.

David Marciel.

# API definition

## OpenAPI vs Swagger
OAS 3.0 => 2017 => Specification => relatively stable

SWAGGER => one of many implementations => there be dragons!

## OpenAPI definitions for de project

### Live

https://emea-academy-2022-java.azurewebsites.net/swagger-ui/index.html

### Offline (20220428)

```
{
   "openapi":"3.0.1",
   "info":{
      "title":"OpenAPI definition",
      "version":"v0"
   },
   "servers":[
      {
         "url":"http://localhost:8080",
         "description":"Generated server url"
      }
   ],
   "paths":{
      "/api/tasks":{
         "get":{
            "tags":[
               "todo-app-controller"
            ],
            "operationId":"getAllRecordsByCreationDate",
            "responses":{
               "200":{
                  "description":"OK",
                  "content":{
                     "*/*":{
                        "schema":{
                           "$ref":"#/components/schemas/TaskList"
                        }
                     }
                  }
               }
            }
         },
         "post":{
            "tags":[
               "todo-app-controller"
            ],
            "operationId":"addTask",
            "requestBody":{
               "content":{
                  "application/json":{
                     "schema":{
                        "$ref":"#/components/schemas/Task"
                     }
                  }
               },
               "required":true
            },
            "responses":{
               "200":{
                  "description":"OK",
                  "content":{
                     "*/*":{
                        "schema":{
                           "$ref":"#/components/schemas/Task"
                        }
                     }
                  }
               }
            }
         }
      },
      "/api/tasksChangeStatus/{taskid}":{
         "get":{
            "tags":[
               "todo-app-controller"
            ],
            "operationId":"changeStatus",
            "parameters":[
               {
                  "name":"taskid",
                  "in":"path",
                  "required":true,
                  "schema":{
                     "type":"integer",
                     "format":"int32"
                  }
               }
            ],
            "responses":{
               "200":{
                  "description":"OK",
                  "content":{
                     "*/*":{
                        "schema":{
                           "$ref":"#/components/schemas/Task"
                        }
                     }
                  }
               }
            }
         }
      },
      "/api/tasks/{taskid}":{
         "get":{
            "tags":[
               "todo-app-controller"
            ],
            "operationId":"getTaskById",
            "parameters":[
               {
                  "name":"taskid",
                  "in":"path",
                  "required":true,
                  "schema":{
                     "type":"string"
                  }
               }
            ],
            "responses":{
               "200":{
                  "description":"OK",
                  "content":{
                     "*/*":{
                        "schema":{
                           "$ref":"#/components/schemas/Task"
                        }
                     }
                  }
               }
            }
         },
         "delete":{
            "tags":[
               "todo-app-controller"
            ],
            "operationId":"deleteTaskById",
            "parameters":[
               {
                  "name":"taskid",
                  "in":"path",
                  "required":true,
                  "schema":{
                     "type":"string"
                  }
               }
            ],
            "responses":{
               "200":{
                  "description":"OK"
               }
            }
         }
      }
   },
   "components":{
      "schemas":{
         "Task":{
            "type":"object",
            "properties":{
               "id":{
                  "type":"integer",
                  "format":"int32"
               },
               "title":{
                  "type":"string"
               },
               "description":{
                  "type":"string"
               },
               "status":{
                  "type":"integer",
                  "format":"int32"
               },
               "dueDate":{
                  "type":"string",
                  "format":"date-time"
               },
               "createdDate":{
                  "type":"string",
                  "format":"date-time"
               }
            }
         },
         "TaskList":{
            "type":"object",
            "properties":{
               "tasks":{
                  "type":"array",
                  "items":{
                     "$ref":"#/components/schemas/Task"
                  }
               },
               "counter":{
                  "type":"integer",
                  "format":"int32"
               }
            }
         }
      }
   }
}
```


# Command line help

1.- local(&azure) compiling: mvn clean install

2.- local run against H2:  mvn spring-boot:run "-Dspring-boot.run.profiles=local"

3.- local run against mySQL on azure: mvn spring-boot:run "-Dspring-boot.run.arguments=--spring_datasource_password=SwoAcademy2022Java!"


# FMENDEZ

## MYSQL <> H2 type concordance

https://dev.mysql.com/doc/refman/8.0/en/integer-types.html               
https://www.h2database.com/html/datatypes.html

## Where to look for dependencies

https://mvnrepository.com/

## GIT config

https://git-scm.com/book/en/v2/Getting-Started-First-Time-Git-Setup



## HATEOAS

Hypermedia As The Engine Of Application State 

https://spring.io/projects/spring-hateoas

## OpenAPI Help & Tools

https://swagger.io/resources/articles/best-practices-in-api-design/

https://swagger.io/docs/specification/about/

https://github.com/swagger-api/swagger-editor => online client/server generator

https://github.com/swagger-api/swagger-codegen => CLI client/server generator

But: All these resources/tool will are provided on a "as-is" basis. Nothing is guaranteed, so be flexible and adapt... or sign a contract!

