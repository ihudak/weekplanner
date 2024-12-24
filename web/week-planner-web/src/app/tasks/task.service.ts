import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";
import {Task} from "./task";

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private baseURL=`${environment.tasksSrvUrl}/tasks`;

  constructor(private _http: HttpClient) { }

  addTask(task: Task): Observable<any> {
    return this._http.post(this.baseURL, task);
  }

  updateTask(taskId: string, task:Task): Observable<any> {
    return this._http.put(`${this.baseURL}/${taskId}`, task);
  }

  getTaskList(): Observable<any> {
    return this._http.get(this.baseURL);
  }

  deleteTask(taskId: string): Observable<any> {
    return this._http.delete(`${this.baseURL}/${taskId}`);
  }
}
