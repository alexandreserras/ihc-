package com.ihc.readpal;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import org.javatuples.Sextet;

public class Book implements Parcelable {
    private Uri Uri_book_text;
    private Uri Uri_book_image;
    private Uri Uri_default_book_image;
    private String titulo;
    private String author;
    private String cdate;

    public Book(Uri Uri_book_text,Uri Uri_book_image,String titulo,String author,String date){
        this.Uri_book_text=Uri_book_text;
        this.Uri_book_image=Uri_book_image;
        this.Uri_default_book_image=Uri_book_image;
        this.titulo=titulo;
        this.author=author;
        this.cdate=date;
    }

    public Book(){
    }

    protected Book(Parcel in) {
        Uri_book_text = in.readParcelable(Uri.class.getClassLoader());
        Uri_book_image = in.readParcelable(Uri.class.getClassLoader());
        Uri_default_book_image = in.readParcelable(Uri.class.getClassLoader());
        titulo = in.readString();
        author = in.readString();
        cdate = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public void setUri_book_text(Uri Uri_book_text){
        this.Uri_book_text=Uri_book_text;
    }
    public void setUri_book_image(Uri Uri_book_image){
        this.Uri_book_image=Uri_book_image;
    }
    public void settitulo(String titulo){
        this.titulo=titulo;
    }
    public void setauthor(String author){
        this.author=author;
    }
    public void setcdate(String cdate){
        this.cdate=cdate;
    }
    public void setUri_default_book_image(Uri Uri_book_image){
        this.Uri_default_book_image=Uri_book_image;
    }


    public Uri getUri_book_text(){
        return this.Uri_book_text;
    }
    public Uri getUri_book_image(){
        return this.Uri_book_image;
    }
    public Uri getUri_default_book_image(){
        return this.Uri_default_book_image;
    }
    public String gettitulo(){
        return this.titulo;
    }
    public String getauthor(){
        return this.author;
    }
    public String getcdate(){
        return this.cdate;
    }

    public Sextet getall(){
        Sextet<Uri, Uri,Uri, String, String, String> six = new Sextet<>(Uri_book_text,Uri_default_book_image,Uri_book_image,titulo,author,cdate);
        return six;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(Uri_book_text, flags);
        dest.writeParcelable(Uri_book_image, flags);
        dest.writeParcelable(Uri_default_book_image, flags);
        dest.writeString(titulo);
        dest.writeString(author);
        dest.writeString(cdate);
    }
}
