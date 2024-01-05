import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from "@angular/forms";
import {TaskState} from "../task.state";
import {TaskService} from "../task.service";
import {TaskCategory} from "../task.category";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {CoreService} from "../../core/core.service";

@Component({
  selector: 'app-task-add-edit',
  templateUrl: './task-add-edit.component.html',
  styleUrls: ['./task-add-edit.component.scss']
})

export class TaskAddEditComponent implements OnInit {
  taskForm: FormGroup;

  states: string[] = Object.keys(TaskState);

  categories: string[] = Object.keys(TaskCategory).slice(Object.keys(TaskCategory).length / 2);

  constructor(
    private _fb: FormBuilder,
    private _taskService: TaskService,
    private _dialogRef: MatDialogRef<TaskAddEditComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private _coreService: CoreService
  ) {
    this.taskForm = this._fb.group({
      taskId:        undefined,
      categoryId:    0,
      title:         '',
      description:   '',
      cronSchedule:  '',
      state:         '',
      addedPriority: 0
    })
  }

  ngOnInit() {
    this.taskForm.patchValue(this.data);
  }

  onFormSubmit() {
    if(this.taskForm.valid) {
      if (this.data) {
        this._taskService.updateTask(this.data.taskId, this.taskForm.value).subscribe({
          next: (val: any) => {
            this._coreService.openSnackBar('Task updated successfully');
            this._dialogRef.close(true);
          },
          error: (err: any) => {
            console.error(err);
          }
        });
      } else {
        this._taskService.addTask(this.taskForm.value).subscribe({
          next: (val: any) => {
            this._coreService.openSnackBar('Task created successfully');
            this._dialogRef.close(true);
          },
          error: (err: any) => {
            console.error(err);
          }
        });
      }
    }
  }
}
