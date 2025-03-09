import {TaskLink} from "./task.link";
import {TaskState} from "./task.state";

export class Task {
  taskId: string | undefined;
  categoryId: number = 0;
  title: string = '';
  description: string | undefined;
  state: TaskState = TaskState.PREP;
  cronExpression: string = '';
  taskLinks: TaskLink[] | undefined;
  addedPriority: number = 0;

  isValid(): boolean {
    let anyValidLink: boolean = false;

    if (this.taskLinks !== undefined) {
      this.taskLinks.forEach((link, index, array) => {
        if (link.isValid()) {
          anyValidLink = true;
        }
      })
    }
    if (!anyValidLink) {
      this.taskLinks = undefined;
    }

    return this.categoryId >= 0           &&
      this.title.trim().length > 0        &&
      this.cronExpression.trim().length > 0 &&
      this.addedPriority >= -50 && this.addedPriority <= 50;
  }
}
