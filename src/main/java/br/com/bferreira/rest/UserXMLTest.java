package br.com.bferreira.rest;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.internal.path.xml.NodeImpl;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserXMLTest {

    private static RequestSpecification reqSpec;
    private static ResponseSpecification resSpec;

    @BeforeClass //Será setada as configurações do método antes de serem rodados os testes da classe
    public static void setup() {
        RestAssured.baseURI = "https://restapi.wcaquino.me";
//        RestAssured.port = 443;
//        RestAssured.basePath = "/v2";
        RequestSpecBuilder reqBuilder = new RequestSpecBuilder(); // especificações para requests, usada quando é necessário usar em todos os cenários de teste
        reqBuilder.log(LogDetail.ALL); //É gerado um log de toda a request construída
        reqSpec = reqBuilder.build();

        ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();
        resBuilder.expectStatusCode(200); //é esperado um status code 200 vindo da response
        resSpec = resBuilder.build();

        RestAssured.requestSpecification = reqSpec;//adiciona o RequestSpecification a uma variável do RestAssured fazendo com que todos os testes herdem essas configurações
        RestAssured.responseSpecification = resSpec;//adiciona o ResponseSpecification a uma variável do RestAssured fazendo com que todos os testes herdem essas configurações
    }

    @Test
    public void devoTrabalharComXML() {
        given()
                .when()
                .get("/usersXML/3")
                .then()
                .body("user.name", is("Ana Julia"))//Obter o valor do atributo name do XML
                .body("user.@id", is("3"))//Obter o valor do atributo que está dentro da tag user, todos os valores do XML são strings
                .body("user.filhos.name.size()", is(2))//Obter uma lista com os names dos filhos e validar o tamanho
                .body("user.filhos.name[0]", is("Zezinho"))
                .body("user.filhos.name[1]", is("Luizinho"))
                .body("user.filhos.name", hasItem("Luizinho"))
                .body("user.filhos.name", hasItems("Zezinho", "Luizinho"));
    }

    @Test
    public void devoTrabalharComXMLNoRaiz() {
        given()
                .when()
                .get("/usersXML/3")
                .then()

                .rootPath("user")//Indica o user com nó raiz dispensando o uso nas chamadas
                .body("name", is("Ana Julia"))
                .body("@id", is("3"))

                .rootPath("user.filhos")//Indica o user.filhos com nó raiz dispensando o uso nas chamadas
                .body("name.size()", is(2))

                .detachRootPath("filhos")//Retira o filhos do nó raiz, voltando a ser somente user
                .body("filhos.name[0]", is("Zezinho"))
                .body("filhos.name[1]", is("Luizinho"))

                .appendRootPath("filhos")//Adiciona o filhos a indicação da raiz. Resultado: user.filhos novamente
                .body("name", hasItem("Luizinho"))
                .body("name", hasItems("Zezinho", "Luizinho"));
    }

    @Test
    public void devoFazerPesquisasAvancadasXML() {
        given()
                .when()
                .get("/usersXML")
                .then()
                .body("users.user.size()", is(3))//Valida quantidade de objetos user no XML
                .body("users.user.findAll{it.age.toInteger() <= 25}.size()", is(2))//Valida quantidade com idade <= 25
                .body("users.user.@id", hasItems("1", "2", "3"))//Valida a coleção recebida com os números de ids
                .body("users.user.find{it.age == 25}.name", is("Maria Joaquina"))//Pesquisa o name do objeto que contem age == 25 e valida o nome
                .body("users.user.findAll{it.name.contains('n').toString()}.name", hasItems("Maria Joaquina", "Ana Julia"))//Pesquisa e retorna os names que possuem a letra 'n' e valida a lista dos nomes
                .body("users.user.salary.find{it != null}", is("1234.5678"))//Pesquisa o objeto que contem o atributo salary diferente de null e valida o valor
                .body("users.user.salary.find{it != null}.toDouble()", is(1234.5678d))//Pesquisa o objeto que contem o atributo salary diferente de null e valida o valor como double
                .body("users.user.age.collect{it.toInteger() * 2}", hasItems(40, 50 , 60))//Pesquisa as ages, altera para inteiro, multiplica por dois e valida a lista com o resultado esperado
                .body("users.user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA"));//Pesquisa o atributo name que inicia com 'Maria', altera para UpperCase e valida
    }

    @Test
    public void devoFazerPesquisasAvancadasXMLeJava() {
        ArrayList<NodeImpl> nomes = given()
                .when()
                .get("/usersXML")
                .then()//É passado para o arrayList uma coleção que contém os nomes que possuem a letra 'n', depois é mais facil trabalhar com os dados
                .extract().path("users.user.name.findAll{it.toString().contains('n')}");
        Assert.assertEquals(nomes.size(), 2);
        Assert.assertEquals("MARIA JOAQUINA", nomes.get(0).toString().toUpperCase());
        Assert.assertTrue("ANA JULIA".equalsIgnoreCase(nomes.get(1).toString()));
    }

    @Test
    public void devoFazerPesquisasAvancadasXMLcomXPath() {
        given()
                .when()
                .get("/usersXML")
                .then()
                .body(hasXPath("count(/users/user)", is("3")))//Faz a conta e valida a quantidade de user, o matcher deve ficar dentro do hasXPath
                .body(hasXPath("/users/user[@id=1]"))//Valida se existe o user com id = 1 na estrutura
                .body(hasXPath("//user[@id=2]"))//Valida se existe o user com id = 2 na estrutura, já partindo do user com o //
                .body(hasXPath("//name[text() = 'Luizinho']/../../name", is("Ana Julia")))//Encontra o campo filho, para depois encontrar o name do Pai na estrutura
                .body(hasXPath("//name[text() = 'Ana Julia']/following-sibling::filhos", allOf(containsString("Zezinho"), containsString("Luizinho"))))//Encontra o objeto com name Ana Julia, procura entre os atributos irmãos o atributo filhos
                .body(hasXPath("/users/user/name", is("João da Silva")))//Retorna o primeiro valor name encontrado e valida
                .body(hasXPath("//name", is("João da Silva")))//Retorna o primeiro valor name encontrado e valida
                .body(hasXPath("/users/user[2]/name", is("Maria Joaquina")))//Retorna o segundo valor name e valida, pois procura no user com id 2
                .body(hasXPath("/users/user[last()]/name", is("Ana Julia")))//Retorna o ultimo valor name e valida
                .body(hasXPath("count(/users/user/name[contains(., 'n')])", is("2")))//Valida a quantidade de nomes que contém a letra 'n'
                .body(hasXPath("//user[age < 24]/name", is("Ana Julia")))//Valida o user com age < 24
                .body(hasXPath("//user[age > 24 and age < 30]/name", is("Maria Joaquina")))
                .body(hasXPath("//user[age > 24][age < 30]/name", is("Maria Joaquina")));
    }

    @Test
    public void devoTrabalharComXMLAtributosEstaticos() {
        given()
                .when()
                .get("/usersXML/3")
                .then()
                .body("user.name", is("Ana Julia"))//Obter o valor do atributo name do XML
                .body("user.@id", is("3"))//Obter o valor do atributo que está dentro da tag user, todos os valores do XML são strings
                .body("user.filhos.name.size()", is(2))//Obter uma lista com os names dos filhos e validar o tamanho
                .body("user.filhos.name[0]", is("Zezinho"))
                .body("user.filhos.name[1]", is("Luizinho"))
                .body("user.filhos.name", hasItem("Luizinho"))
                .body("user.filhos.name", hasItems("Zezinho", "Luizinho"));
    }
}
