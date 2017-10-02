# Mini-Projeto-Restful-API
==========================

Descrição
---------

RESTFul API usando o framework Spark para Java e o banco MongoDB. Fornece funções que permitem interagir com sensores e streams de sensores. Pode-se assim registrar sensores, streams e informação medida pelos sensores em uma certa stream.

Instalação
----------
Este projeto necessita de Java 8 para ser usado pois usa Lambda Expressions.
A linguagem Scala também deve estar instalada em seu sistema para este projeto.
O banco de dados MongoDB deve estar instalado (feito com o MongoDB 3.4.9). 
Este projeto é um projeto Maven, após a importação do repositório deixe o Maven baixar suas dependências para então executar o projeto.
É possível ver a versão de todas as dependências do Maven no arquivo pom.xml, assim como mudá-las.


Funções
-------

Todas as respostas são em formato JSON. A data enviada por POST requests também deve estar em formato JSON.

------------------------------
Consultar unidades de grandeza
Descrição: Retorna todas as unidades de grandeza cadastradas no banco de dados.
URL: /units
Método: GET
Parâmetros URL: Nenhum.
Parâmetros de Data: Nenhum.
Resposta: Lista JSON de unidades.

[
 {
 "oid": [string],
 "label": [string]
 }
 {
 "oid": [string],
 "label": [string]
 },
 {
 "oid": [string],
 "label": [string]
 },
 ...
]

------------------------------
Consultar sensores de um usuário
Descrição: Pega todos os sensores de um usuário, assim como as streams destes sensores.
URL: /sensors/:username
Método: POST
Parâmetros URL: username=[String]
Parâmetros de Data: Nenhum.
Resposta: Lista JSON de sensores, sendo streams também uma lista JSON mas de streams.

[
 { 
 "oid": [String],
 "key": [String],
 "label": [String],
 "description": [String],
 "streams": [
     {
     "oid": [String],
     "key": [String],
     "label": [String],
     "unit": [String],
     "sensor": [String],
     "totalSize": [long]
     },
     ...
   ]
 },
 ...
]

------------------------------
Consulta de um sensor específico
Descrição: Pega um sensor específico baseado no atributo key do sensor (chave auto-gerada durante registro).
URL: /sensors?key=:key
Método: GET
Parâmetros URL: key=[String]
Parâmetros de Data: Nenhum.
Resposta:

Consultar sensores de um usuário
Descrição: Pega todos os sensores de um usuário, assim como as streams destes sensores.
URL: /sensors/:username
Método: Post
Parâmetros URL:
Parâmetros de Data:
Resposta:

Consultar sensores de um usuário
Descrição: Pega todos os sensores de um usuário, assim como as streams destes sensores.
URL: /sensors/:username
Método: Post
Parâmetros URL:
Parâmetros de Data:
Resposta:

Consultar sensores de um usuário
Descrição: Pega todos os sensores de um usuário, assim como as streams destes sensores.
URL: /sensors/:username
Método: Post
Parâmetros URL:
Parâmetros de Data:
Resposta:

Consultar sensores de um usuário
Descrição: Pega todos os sensores de um usuário, assim como as streams destes sensores.
URL: /sensors/:username
Método: Post
Parâmetros URL:
Parâmetros de Data:
Resposta:
