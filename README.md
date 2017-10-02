# Mini-Projeto-Restful-API

RESTFul API usando o framework Spark para Java e o banco MongoDB. Fornece funções que permitem interagir com sensores e streams de sensores. Pode-se assim registrar sensores, streams e informação medida pelos sensores em uma certa stream.  

Instalação
----------
Este projeto necessita de Java 8 para ser usado pois usa Lambda Expressions.  
A linguagem Scala também deve estar instalada em seu sistema para este projeto.  
O banco de dados MongoDB deve estar instalado (feito com o MongoDB 3.4.9).   
Este projeto é um projeto Maven, após a importação do repositório deixe o Maven baixar suas dependências para então executar o projeto.  
É possível ver a versão de todas as dependências do Maven no arquivo pom.xml, assim como mudá-las.  


Funções  
---

Todas as respostas são em formato JSON. A data enviada por POST requests também deve estar em formato JSON. Se ocorrer algum erro na chamada de qualquer função, um JSON com apenas um atributo, "status", será enviado informando sobre o problema.


### Consultar unidades de grandeza
  
* Descrição: Retorna todas as unidades de grandeza cadastradas no banco de dados.
* URL: /units
* Método: GET
* Parâmetros URL: Nenhum.
* Parâmetros de Data: Nenhum.
* Resposta: Lista JSON de unidades.
```
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
```

### Consultar sensores de um usuário  

* Descrição: Pega todos os sensores de um usuário, assim como as streams destes sensores.
* URL: /sensors/:username  
* Método: GET  
* Parâmetros URL: username=[String]  
* Parâmetros de Data: Nenhum.  
* Resposta: Lista JSON de sensores, sendo streams também uma lista JSON mas de streams.  
```
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
```

### Consulta de um sensor específico

* Descrição: Pega um sensor específico definido por uma key (chave auto-gerada durante registro do sensor).
* URL: /sensors?key=:key
* Método: GET 
* Parâmetros URL: key=[String] 
* Parâmetros de Data: Nenhum.  
* Resposta: JSON representando o sensor. streams é uma lista JSON de streams associadas àquele sensor. data é uma lista JSON de data associada a uma stream que contém apenas as 5 datas mais recentes.

```
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
   "totalSize": [long],
   "data": [
       {
       "timestamp": [long],
       "data": [double]
       },
       {
       "timestamp": [long],
       "data": [double]
       },
       ...
       ]
   },
   ...
   ]
 }
```

### Consulta de uma stream específica

* Descrição: Pega uma stream específica definida por uma key (chave auto-gerada durante registro do sensor).
* URL: /streams?key=:key
* Método: GET  
* Parâmetros URL: key=[String] 
* Parâmetros de Data: Nenhum.  
* Resposta: JSON representando a stream. data é uma lista JSON de todas as datas associada a uma stream.

```

   {
   "oid": [String],
   "key": [String],
   "label": [String],
   "unit": [String],
   "sensor": [String],
   "totalSize": [long],
   "data": [
       {
       "timestamp": [long],
       "data": [double]
       },
       {
       "timestamp": [long],
       "data": [double]
       },
       ...
       ]
   }

```

### Registrar sensor

* Descrição: Registra um sensor no banco de dados.
* URL: /sensors
* Método: POST  
* Parâmetros URL: Nenhum.
* Parâmetros de Data: label e description do sensor.

```
{
 "label": [String],
 "description": [String],
}

```


* Resposta: JSON repetindo label e description junto com a key e oid do sensor.

```
{
 "oid": [String],
 "key": [String],
 "label": [String],
 "description": [String],
}
```

### Registrar stream em um sensor

* Descrição: Registra uma stream em um sensor dada a chave (key) do sensor.
* URL: /sensors?key=:key
* Método: POST  
* Parâmetros URL: key=[String].
* Parâmetros de Data: label e oid da unidade da stream.

```
{
 "label": [String],
 "unit": [String]
}
```

* Resposta: JSON repetindo label e unit, assim como informando key, oid, oid do sensor e tamanho atual da stream.

```
{
 "oid": [String],
 "key": [String],
 "label": [String],
 "unit": [String],
 "sensor": [String],
 "totalSize": [long]
}
```

### Publicar data em uma stream

* Descrição: Publica uma data em uma stream dada a chave da stream (key).
* URL: /sensors?key=:key
* Método: POST  
* Parâmetros URL: key=[String].
* Parâmetros de Data: timestamp e valor da medição.

```
{
 "timestamp": [long],
 "value": [double]
}
```

* Resposta: JSON repetindo timestamp e value, assim como oid da data registrada.

```
{
 "oid": [String],
 "timestamp": [long],
 "value": [double]
}
```

