import {Component, OnInit, ViewChild} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {TaskAddEditComponent} from "./tasks/task-add-edit/task-add-edit.component";
import {environment} from "../environments/environment";
import {TaskService} from "./tasks/task.service";
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {TaskCategory} from "./tasks/task.category";
import {CoreService} from "./core/core.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = environment.title;
  buildVer = environment.verGUI;
  buildDate = environment.dateGUI;
  tenantId = environment.selectedTenant;

  displayedColumns: string[] = ['taskId', 'categoryId', 'title', 'description', 'state', 'cronSchedule', 'addedPriority', 'action'];
  dataSource!: MatTableDataSource<any>;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private _dialog: MatDialog,
    private _taskService: TaskService,
    private _coreService: CoreService
  ) {}

  ngOnInit() {
    this.getTaskList();
  }

  openAddEditTaskForm() {
    const dialogRef = this._dialog.open(TaskAddEditComponent);
    dialogRef.afterClosed().subscribe({
      next: (val) => {
        if(val) {
          this.getTaskList();
        }
      }
    });
  }

  openEditTaskForm(data: any) {
    const dialogRef = this._dialog.open(TaskAddEditComponent, {
      data
    });
    dialogRef.afterClosed().subscribe({
      next: (val) => {
        if(val) {
          this.getTaskList();
        }
      }
    })
  }

  getTaskList() {
    this._taskService.getTaskList().subscribe({
      next: (res) => {
        this.dataSource = new MatTableDataSource(res.content);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
      },
      error: (err) => {
        console.error(err);
      }
    });
  }

  deleteTask(taskId: string) {
    this._taskService.deleteTask(taskId).subscribe({
      next: (res) => {
        this._coreService.openSnackBar('Task deleted!');
        this.getTaskList();
      },
      error: console.error
    });
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  protected readonly TaskCategory = TaskCategory;
}
