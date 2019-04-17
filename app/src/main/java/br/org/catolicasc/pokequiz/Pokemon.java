package br.org.catolicasc.pokequiz;

public class Pokemon {
    private int id;
    private String nome;
    private String imgUrl;

    public Pokemon(int id, String nome, String imgUrl) {
        this.id = id;
        this.nome = nome;
        this.imgUrl = imgUrl;
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
