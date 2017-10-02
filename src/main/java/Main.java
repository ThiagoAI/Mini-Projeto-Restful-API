/*	API de Sensores e Streams
 *  Autor: Thiago Anders Imhoff
 *  
 *  VER README.md PARA DOCUMENTAÇÃO !!!
 *  
 *  Ao final da main, documentação mais simples.
 */

import static spark.Spark.*;

import sensorsandstreams.controller.DataController;
import sensorsandstreams.controller.SensorController;
import sensorsandstreams.controller.StreamController;
import sensorsandstreams.controller.UnitController;
import sensorsandstreams.controller.UserController;
import sensorsandstreams.service.DataService;
import sensorsandstreams.service.SensorService;
import sensorsandstreams.service.StreamService;
import sensorsandstreams.service.UnitService;
import sensorsandstreams.service.UserService;

public class Main {
	public static void main(String[] args) throws Throwable {

		//Restful api escutará em localhost:8080 por default
		port(8080);
		ipAddress("localhost");
		
		//Ativando controllers
		new SensorController(new SensorService());
		new StreamController(new StreamService());
		new UnitController(new UnitService());
		new DataController(new DataService());
		//new UserController(new UserService());
	
	}
	
}


/*  NOTAS DA IMPLEMENTAÇÃO:
*  -Projeto usa Maven. MongoDB tem que estar instalado e executando.
*  Versão do java tem que ser 8.
*  
*  -Feita usando Java Spark e banco de dados MongoDB, assim como
*  a biblioteca Gson (todos importados no arquivo pom.xml).
*   
*  -Versões do Java Spark e MongoDB para Java podem ser vistas no 
*  arquivo pom.xml.
*  
*  -RESTFul API usa o  endereço "localhost:8080" por default. É possível
*  trocar isto no arquivo Main.java.
*  
*  -----
*  
*  DESCRIÇÃO: 
*  RESTFul API que permite registro de sensores e streams, assim
*  como publicar informações nestas streams. Podemos também obter 
*  informações sobre todas estas entidades. Inputs de requests
*  POST assim como todos os outputs estão em formato JSON.
* 
*  FUNÇÕES DA API:
*  Nota - Em caso de erros, JSON de resposta apenas terá um atributo status,
*  que contém informações sobre qual foi o problema.
*  
*  
*  [GET] Consultar unidades de grandeza
*  Descrição: Pega todas as unidades de grandeza registradas no sistema.
*  Request: Nenhum.
*  Endereço: /units
*  Response: Lista Json com cada unit tendo os atributos oid (chave do
*  banco) e label (nome da unidade).
*  
*  [GET] Consultar sensores de um usuário
*  Descrição: Pega todos os sensores do usuário passado. Também inclui
*  todas as streams de cada sensor.
*  Request: Nome do usuário (URL Path como no Endereço desta função)
*  
*  Endereço: /sensors/:nomedousuário
*  
*  Response: Lista JSON de sensores, cada sensor tem os atributos oid,
*  key (chave auto-gerada), label, description e streams.
*  streams é uma lista JSON de streams do sensor, cada stream contém os atributos
*  oid (chave do banco), key (chave auto-gerada), label, unit (oid da unidade
*  da stream), sensor (oid do sensor) e totalSize (número de datas publicadas 
*  na stream).
*  
*  Nota: Devido ao escopo do mini-projeto, todo sensor é registrado para o
*  usuário "Thiago".
*  -----
*  
*  [GET] Consulta de um sensor específico.
*  Descrição: Pega as informações de um sensor específico, assim como todas
*  as suas streams. Cada stream só irá ter as 5 datas mais recentes.
*  Request: Chave auto-gerada do sensor a ser consultado como query argument: key.
*  
*  Endereço: /sensors (passar chave com ?key=)
*  
*  Response: JSON do sensor, contendo os atributos oid (chave do banco), key
*  (chave auto-gerada), label, descrption e streams. 
*  streams é uma lista JSON de streams do sensor, cada stream contém os atributos
*  oid (chave do banco), key (chave auto-gerada), label, unit (oid da unidade
*  da stream), sensor (oid do sensor), totalSize (número de datas publicadas 
*  na stream) e data.
*  data é uma lista JSON de datas da stream, cada data tem os atributos
*  timestamp (tempo Unix da medida) e data (valor medido).
*   
*  -----
*   
*  [GET] Consultar dados de uma Stream específica.
*  Descrição: Pega as informações de uma stream específica, assim como
*  as datas que foram publicadas naquela stream. 
*  
*  Request: Chave auto-gerada da stream a ser consultada como query argument: key.
*  
*  Endereço: /streams (passar chave com ?key=)
*  
*  Response: JSON da stream que contém os atributos oid (chave do banco), 
*  key (chave auto-gerada), label, unit (oid da unidade da stream), 
*  sensor (oid do sensor), totalSize (número de datas publicadas 
*  na stream) e data.
*  data é uma lista JSON de datas da stream, cada data tem os atributos
*  timestamp (tempo Unix da medida) e data (valor medido).
*  
*  -----
*  
*  [POST] Registrar sensor
*  Descrição: Registra um sensor no banco de dados.
*  Request: JSON com os atributos "label" e "description" referentes ao sensor.
*  
*  Endereço: /sensors
*  
*  Response: JSON contendo os atributs oid (chave do banco do sensor cadastrado),
*  key (chave auto-gerada para aquele sensor), label e description.
* 
* 	Nota: Devido ao escopo do mini-projeto, todo sensor é registrado para o
*  usuário "Thiago".
*  
*  -----
* 
*  [POST] Registrar stream para sensor
*  Descrição: Registra uma stream em um sensor passado.
*  Request: JSON com os atributos label e unit (unit é a chave do banco da unidade
*  desta stream), assim como a key do sensor que deve ser passada como query
*  argument: key.
*  Endereço: /streams (passar chave com ?key=)
*  Response: JSON com os atributos oid (chave do banco da stream inserida),
*  key (chave auto-gerada para a stream), label, unit (chave do banco da unidade),
*  sensor (chave do banco do sensor ao qual a stream foi registrada) e totalSize.
* 
*  -----
*  
*  [POST] Publicar dados em um Stream
*  Descrição: Publica data na stream passada.
*  Request: JSON com os atributos timestamp e value (tempo a valor da medição).
*  Também é necessário a chave auto-gerada da stream como query parameter: key.
*  
*  Endereço: /data (passar chave com ?key=)
*  
*  Response: JSON com os atributos oid (chave do banco da data publicada),
*  timestamp e value.
*/
