package biblioteca.model;

public class Multa {

    private int id;
    private double valor;
    private boolean isQuitado;
    private Usuario usuario; 
    private Livro livro;    

    public Multa() {
    }

    
    public Multa(int id, double valor, boolean isQuitado, Usuario usuario, Livro livro) {
        this.id = id;
        this.valor = valor;
        this.isQuitado = isQuitado;
        this.usuario = usuario;
        this.livro = livro;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public boolean isIsQuitado() {
        return isQuitado;
    }

    public void setIsQuitado(boolean isQuitado) {
        this.isQuitado = isQuitado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }
}