Book database example
=======

####Goal:
Let us have book catalog, with standard book attribute. You can take book information from any book e-store (i.e. amazon.com) as an example of book attribute. Preconditions: book catalog is stored in DB and DB size is big. It also uses several tables to store all book information. Quick response is the main requirements.

####Task:
Implement back-end service with CRUD functions.

####Recommendations:
Think about it as task with 300 line of code with unlimited budget.

####Implementation:
RESTful Web Service specification JAX-RS 2.0 and JPA 2.1 using RESTEasy 3, jackson 2, Hibernate 4.3, Derby, jUnit.  
Application should be deployed on WildFly 8.2.
