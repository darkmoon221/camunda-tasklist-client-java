[![Community Extension](https://img.shields.io/badge/Community%20Extension-An%20open%20source%20community%20maintained%20project-FF4700)](https://github.com/camunda-community-hub/community)
[![](https://img.shields.io/badge/Lifecycle-Incubating-blue)](https://github.com/Camunda-Community-Hub/community/blob/main/extension-lifecycle.md#incubating-)

# Camunda TaskList Client

This project is designed to simplify communication between a java backend and the Camunda 8 task list. This project is still a draft.

# How to use the client

Simply build a CamundaTaskListClient that takes an authentication and the tasklist url as parameters.

```
SimpleAuthentication sa = new SimpleAuthentication("demo", "demo", "http://localhost:8081");
CamundaTaskListClient client = new CamundaTaskListClient.Builder().authentication(sa)
	taskListUrl("http://localhost:8081").build();


client.getTasks(true, "demo", true, 50);
client.unclaim("506493039354");
```

For the moment, the only auth available is the Simple one. 


# TODO

- Add a jwt keycloak auth
- Manage graphql queries with a dedicated lib
