package br.com.bferreira.rest;

import io.restassured.http.ContentType;
import io.restassured.path.xml.XmlPath;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthTest {

    @Test
    public void deveAcessarSWAPI() {
        given()
            .log().all()
        .when()
            .get("https://swapi.dev/api/people/1")
        .then()
            .log().all()//acessa a api externa do star wars valida o status code do GET e valida o valor do name da response obtida
            .statusCode(200)
            .body("name", is("Luke Skywalker"));
    }

    @Test
    public void deveObterClima() {
        given()
            .log().all()//acessa a api de clima passando como parâmetros, a cidade/país, o token para autenticar(obtido no site) e tipo de unidade desejado
            .queryParam("q", "Porto Alegre,BR")
            .queryParam("appid", "2bfcc88a1dce0ad838e8a88c0863d28d")
            .queryParam("units", "metric")
        .when()
            .get("https://api.openweathermap.org/data/2.5/weather")
        .then()
            .log().all()
            .statusCode(200)//valida o status code, o name da cidade, a longitude e a temperatura.
            .body("name", is("Porto Alegre"))
            .body("coord.lon", is( -51.23f))//Obs: lon está dentro do objeto coord e temp dentro do objeto main
            .body("main.temp", greaterThan( -50)); // Temperatura deve ser maior que -50°C (validação mais realista)
    }

    @Test
    public void naoDeveAcessarSemSenha() {
        given()
            .log().all()
        .when()
            .get("https://restapi.wcaquino.me/basicauth")
        .then()
            .log().all()//tenta acessar a url sem o user e a senha e valida o 401 de unauthorized
            .statusCode(401);
    }

    @Test
    public void deveFazerAutenticacaoBasica() {
        given()
            .log().all()
        .when()//é passado via url o user e password para acesso e logon na pagina
            .get("https://admin:senha@restapi.wcaquino.me/basicauth")
        .then()
            .log().all()
            .statusCode(200)//valida o status code e o body da response
            .body("status", is("logado"));
    }

    @Test
    public void deveFazerAutenticacaoBasica2() {
        given()
            .log().all()//user e password são passados no given ao invés da url
            .auth().basic("admin", "senha")
        .when()
            .get("https://restapi.wcaquino.me/basicauth")
        .then()
            .log().all()
            .statusCode(200)
            .body("status", is("logado"));
    }

    @Test
    public void deveFazerAutenticacaoBasicaComChallenge() {
        given()
            .log().all()
            .auth().preemptive().basic("admin", "senha")
        .when()
            .get("https://restapi.wcaquino.me/basicauth2")
        .then()
            .log().all()
            .statusCode(200)
            .body("status", is("logado"));
    }

    //JWT - Json Web Token - possui dados encriptados com informações sobre o algoritmo usado e o usuário que o token se destina
    @Test
    public void deveFazerAutenticacaoComToken() {

        //login na api
        Map<String, String> login = new HashMap<String, String>();//instancia um objeto HashMap para que sejam inclusas as chaves/valor de email e senha
        login.put("email", "bruno_graves@bol.com.br");
        login.put("senha", "admin");
        String token = given()//valor do token extraído da response da request, é add a uma string
                .log().all()
                .body(login)//é passado o objeto login gerado no body da requisição POST
                .contentType(ContentType.JSON)//especifica-se o JSON para formatar o objeto login no body da requisição
            .when()
                .post("https://barrigarest.wcaquino.me/signin")
            .then()
                .log().all()
                .statusCode(200)
                .extract().path("token");//é extraído o valor da chave token recebida no body na response da request

        //Obter as contas
        given()
            .log().all()//para acessar a url de contas é necessário passar o token via header(tem que ser adicionado o 'JWT' concatenando com o valor do token obtido)
            .header("Authorization","JWT "+ token)
        .when()
            .get("https://barrigarest.wcaquino.me/contas")
        .then()
            .log().all()
            .statusCode(200)//valida se a lista contem a chave nome, com o valor "Conta mesmo nome"
            .body("nome", hasItem("Conta mesmo nome"));
    }

    @Test
    public void deveAcessarAplicacaoWeb() {

        //login
        String cookie = given()
                .log().all()//formParam é usado para add um valor no formulário html utilizando o id/classe/name do elemento html do campo como chave
                .formParam("email", "bruno_graves@bol.com.br")
                .formParam("senha", "admin")
                .contentType(ContentType.URLENC.withCharset("UTF-8"))//é necessário selecionar esse content-type para envio com a codificação de caracteres utf-8
            .when()
                .post("https://seubarriga.wcaquino.me/logar")
            .then()
                .log().all()
                .statusCode(200)//o valor do cookie recebido nos headers na response da request, é extraído e inserido em uma string
                .extract().header("set-cookie");
        //a string de cookie é dividida tendo como ponto de split o '=' e pegando o 2º elemento do array, posição 1
        //após isso a string que sobrou é dividida em duas partes a partir do ';' e depois pegamos o primeiro elemento do array, posição 0
        //com isso obtemos somente o valor do cookie descartando o restante das informações
        cookie = cookie.split("=")[1].split(";")[0];
        System.out.println(cookie);

        //obterConta
        String body = given()
            .log().all()//a chave do cookie é o nome que estava indicado após o set-cookie no header
            .cookie("connect.sid", cookie)//faz o envio do valor do cookie para que mantenha a sessão como logado e não altere novamente para a pagina inicial de login
        .when()
            .get("https://seubarriga.wcaquino.me/contas")
        .then()
            .log().all()
            .statusCode(200)//percorre o html até o nome da conta e valida
            .body("html.body.table.tbody.tr[0].td[0]", is("Conta mesmo nome"))
                .extract().body().asString();//o código Html recebido na response do request get é passado para uma string

        System.out.println("");
        System.out.println("------------------------------ BODY ---------------------------------");
        System.out.println(body);

        System.out.println("");
        System.out.println("------------------------------ VALOR EXTRAÍDO DO HTML ---------------------------------");
        //a string do html da página é convertida para um objeto XmlPath e assim conseguimos percorrer o path novamente no html até o valor desejado e imprimi-lo
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, body);
        System.out.printf(xmlPath.getString("html.body.table.tbody.tr[0].td[0]"));
    }
}
