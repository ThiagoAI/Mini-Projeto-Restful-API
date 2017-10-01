package jsontransformer;

import com.google.gson.Gson;

import spark.ResponseTransformer;

//Classe para transformar a resposta em um post para json
public class JsonUtil {

	public static String toJson(Object object) {
		return new Gson().toJson(object);
	}
	
	public static ResponseTransformer json() {
		return JsonUtil::toJson;
	}
}
