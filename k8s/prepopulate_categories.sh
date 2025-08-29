#!/usr/bin/env sh

server_url="managed.week-planner.com"  # put the host that you defined in ingress.yaml
service_name="categories"

curl -X GET http://$server_url/api/$service_name/api/v1/$service_name -H "Content-Type: application/json"



curl -X GET http://$server_url/api/categories/api/v1/categories/find?name=Graal -H "Content-Type: application/json"
curl -X GET http://$server_url/api/categories/api/v1/categories -H "Content-Type: application/json"

#categories
curl -X POST http://$server_url/api/categories/api/v1/categories -H "Content-Type: application/json" -d '{"name": "Graal", "priority":30, "color":"bb", "userId":1}'
curl -X POST http://$server_url/api/categories/api/v1/categories -H "Content-Type: application/json" -d '{"name": "Grail", "priority":15, "color":"aa", "userId":1}'
curl -X POST http://$server_url/api/categories/api/v1/categories -H "Content-Type: application/json" -d '{"name": "Apps", "priority":10, "color":"cc", "userId":1}'
curl -X POST http://$server_url/api/categories/api/v1/categories -H "Content-Type: application/json" -d '{"name": "TechFit", "priority":20, "color":"zz", "userId":1}'

curl -X POST http://$server_url/api/categories/api/v1/categories/prepopulate -H "Content-Type: application/json" -d '{"name": "TechFit", "priority":20, "color":"zz", "userId":1}'


curl -X GET http://$server_url/api/categories/api/v1/categories -H "Content-Type: application/json"



# tasks
curl -X POST http://$server_url/api/tasks/api/v1/tasks -H "Content-Type: application/json" -d '{"taskId": null, "categoryId": 1, "title": "Solution Demo", "description": "Need an app to compare Node.js vs Java Native", "cronExpression": "2025-08-14T22:00:00.000Z", "state": "IMPL", "addedPriority": 10}'
curl -X POST http://$server_url/api/tasks/api/v1/tasks -H "Content-Type: application/json" -d '{"taskId": null, "categoryId": 2, "title": "Traces pb Grail billing", "description": "Traces powered by Grail billing", "cronExpression": "2025-08-14T22:00:00.000Z", "state": "READY", "addedPriority": 8}'
curl -X POST http://$server_url/api/tasks/api/v1/tasks -H "Content-Type: application/json" -d '{"taskId": null, "categoryId": 3, "title": "Build a Node.js app", "description": "Need an app to compare Node.js vs Java Native", "cronExpression": "2025-08-14T22:00:00.000Z", "state": "DONE", "addedPriority": 3}'
curl -X POST http://$server_url/api/tasks/api/v1/tasks -H "Content-Type: application/json" -d '{"taskId": null, "categoryId": 4, "title": "Kafka support on Ruby", "description": "Kafka support on Ruby", "cronExpression": "2025-08-14T22:00:00.000Z", "state": "PREP", "addedPriority": 15}'
curl -X POST http://$server_url/api/tasks/api/v1/tasks -H "Content-Type: application/json" -d '{"taskId": null, "categoryId": 4, "title": "Bedrock support on Python", "description": "Bedrock support on Python", "cronExpression": "2025-08-14T22:00:00.000Z", "state": "IMPL", "addedPriority": 17}'
curl -X POST http://$server_url/api/tasks/api/v1/tasks -H "Content-Type: application/json" -d '{"taskId": null, "categoryId": 1, "title": "Tech Detection in K8s FS CN", "description": "Technology Detection in K8s FullStack CloudNative", "cronExpression": "2025-08-14T22:00:00.000Z", "state": "READY", "addedPriority": 22}'

