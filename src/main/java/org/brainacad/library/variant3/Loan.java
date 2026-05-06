package org.brainacad.library.variant3;

import org.brainacad.library.status.LoanStatus;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDate;

public class Loan implements Externalizable {
    private Book book;
    private Reader reader;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private boolean returned;

    public Loan() {
    }

    public Loan(Book book, Reader reader, LocalDate loanDate, LocalDate dueDate) {
        this.book = book;
        this.reader = reader;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returned = false;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public void markReturned() {
        returned = true;
    }

    public LoanStatus getStatus() {
        if (returned) {
            return LoanStatus.RETURNED;
        }
        if (dueDate != null && dueDate.isBefore(LocalDate.now())) {
            return LoanStatus.OVERDUE;
        }
        return LoanStatus.ACTIVE;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(book);
        out.writeObject(reader);
        out.writeBoolean(loanDate != null);
        if (loanDate != null) {
            out.writeUTF(loanDate.toString());
        }
        out.writeBoolean(dueDate != null);
        if (dueDate != null) {
            out.writeUTF(dueDate.toString());
        }
        out.writeBoolean(returned);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        book = (Book) in.readObject();
        reader = (Reader) in.readObject();
        loanDate = in.readBoolean() ? LocalDate.parse(in.readUTF()) : null;
        dueDate = in.readBoolean() ? LocalDate.parse(in.readUTF()) : null;
        returned = in.readBoolean();
    }

    @Override
    public String toString() {
        return "Loan{" +
                "book='" + (book != null ? book.getTitle() : "-") + '\'' +
                ", reader='" + (reader != null ? reader.getFullName() : "-") + '\'' +
                ", loanDate=" + loanDate +
                ", dueDate=" + dueDate +
                ", status=" + getStatus() +
                '}';
    }
}

