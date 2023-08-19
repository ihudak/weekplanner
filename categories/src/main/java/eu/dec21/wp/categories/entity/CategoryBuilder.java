package eu.dec21.wp.categories.entity;

public class CategoryBuilder {
    private Category category;

    public CategoryBuilder() {
        this.category = new Category();
    }

    public CategoryBuilder reset() {
        this.category = new Category();
        return this;
    }

    public CategoryBuilder setName(String name) {
        if (this.category == null) {
            this.reset();
        }
        this.category.setName(name);
        return this;
    }

    public CategoryBuilder setColor(String color) {
        if (this.category == null) {
            this.reset();
        }
        this.category.setColor(color);
        return this;
    }

    public CategoryBuilder setPriority(int priority) {
        if (this.category == null) {
            this.reset();
        }
        this.category.setPriority(priority);
        return this;
    }

    public CategoryBuilder setUserId(long userId) {
        if (this.category == null) {
            this.reset();
        }
        this.category.setUserId(userId);
        return this;
    }

    public CategoryBuilder setId(long id) {
        if (this.category == null) {
            this.reset();
        }
        this.category.setId(id);
        return this;
    }

    public Category build() {
        if (this.category == null) {
            this.reset();
        }
        return this.category;
    }
}
