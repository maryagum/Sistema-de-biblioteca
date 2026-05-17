/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.model;

public class Livro {
    private int id;
    private String nome;
    private String isbn;
    private Autor autor; 
    private GeneroLiterario genero; 
    private boolean isdisponivel;

  
    public Livro() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public GeneroLiterario getGenero() {
        return genero;
    }

    public void setGenero(GeneroLiterario genero) {
        this.genero = genero;
    }

    public boolean isIsdisponivel() {
        return isdisponivel;
    }

    public void setIsdisponivel(boolean isdisponivel) {
        this.isdisponivel = isdisponivel;
    }

    @Override
    public String toString() {
        return this.nome;
    }
}