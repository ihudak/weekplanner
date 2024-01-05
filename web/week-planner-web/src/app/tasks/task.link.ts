export class TaskLink {
  url: string = '';
  name: string = '';

  isValid(): boolean {
    this.url = this.url.trim();
    this.name = this.name.trim();
    if (this.name.length == 0) {
      this.name = this.url;
    }
    return this.url.trim().length > 0 && this.name.trim().length > 0;
  }
}
