#!/usr/bin/env sh

server_url="managed.week-planner.com"  # put the host that you defined in ingress.yaml
service_name="categories"

curl -X GET http://$server_url/api/$service_name/api/v1/$service_name -H "Content-Type: application/json"



curl -X GET http://$server_url/api/categories/api/v1/categories/find?name=Graal -H "Content-Type: application/json"
curl -X GET http://$server_url/api/categories/api/v1/categories -H "Content-Type: application/json"

curl -X POST http://$server_url/api/categories/api/v1/categories -H "Content-Type: application/json" -d '{"name": "Graal", "priority":30, "color":"bb", "userId":1}'
curl -X POST http://$server_url/api/categories/api/v1/categories -H "Content-Type: application/json" -d '{"name": "Grail", "priority":15, "color":"aa", "userId":1}'
curl -X POST http://$server_url/api/categories/api/v1/categories -H "Content-Type: application/json" -d '{"name": "Apps", "priority":10, "color":"cc", "userId":1}'
curl -X POST http://$server_url/api/categories/api/v1/categories -H "Content-Type: application/json" -d '{"name": "TechFit", "priority":20, "color":"zz", "userId":1}'

curl -X POST http://$server_url/api/categories/api/v1/categories/prepopulate -H "Content-Type: application/json" -d '{"name": "TechFit", "priority":20, "color":"zz", "userId":1}'


curl -X GET http://$server_url/api/categories/api/v1/categories -H "Content-Type: application/json"
