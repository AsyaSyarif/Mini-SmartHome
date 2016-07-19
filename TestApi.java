import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat; 
import java.lang.reflect.Array;

import org.apache.http.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.*;
import org.apache.http.message.*;
import org.apache.http.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

//��Ҫ������������:
//Apache httpclient
//Google gson
//����������

public class LinpcloudApi{
	private String protocol = "http://";
	
	//���ط�����
	private String host = "192.168.207.1:80";

	//Զ�̷�����
	//private String host = "218.244.138.146:8000";

	//Զ�̷�����2
	//private String host = "";
	
	//ͨ��API��login������¼���Զ�����APIKEY
	private String apikey = null;

	public static void main(String[] args) throws IOException {
		//���鵥����ĳ�����ֵĹ��ܽ��в��ԣ�ע�͵������Ĳ��Բ���

		//��������
		LinpcloudApi api = new LinpcloudApi();

		//������HTTP�������
		System.out.println("<--------HTTP�������-------->");
		List <BasicNameValuePair> params = new ArrayList <BasicNameValuePair> ();
		params.add(new BasicNameValuePair("param1", "value for param1"));
		params.add(new BasicNameValuePair("param2", "value for param2"));

		ResponseInfo result1 = api.http_put("/helper/put", params);
		result1.show();

		ResponseInfo result2 = api.http_post("/helper/post", params);
		result2.show();

		ResponseInfo result3 = api.http_get("/helper/get" + "?info=test" + "&method=get");
		result3.show();
		
		ResponseInfo result4 = api.http_delete("/helper/delete" + "?info=test" + "&method=delete");
		result4.show();
		

		//��¼����
		System.out.println("<--------��¼��login����������-------->");
		User user = api.login_post("linpcloud", "1234");//��¼���û���������
		System.out.println(user.toString());
		user = api.user_get(user.id);//ͨ��ID��ȡ�����û���Ϣ
		System.out.println(user.toString());

/*
		//�豸�ӿڲ���
		System.out.println("<--------�豸��device���ӿڲ���-------->");
		Device [] devices = api.devices_get();//��ȡ�豸�б����û��µ������豸������������
		System.out.println(Arrays.toString(devices));//��ӡ�豸��Ϣ

		//��ȡĳ���豸����ϸ��Ϣ
		Device device = api.device_get(10010);
		System.out.println(device.toString());

		//���һ�����豸����Ҫָ���豸����name
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = formater.format(new   java.util.Date());
		api.devices_post("java-device@" + date, "tags", "device created by java api, at " + date, "locate");

		//ɾ��һ���豸
		//api.device_delete(10010);
*/

/*
		//�������ӿڲ���
		System.out.println("<--------��������sensor���ӿڲ���-------->");
		Sensor[] sensors = api.sensors_get(10012);//ͨ���豸ID��ȡĳ�������豸�����еĴ�����
		System.out.println(Arrays.toString(sensors));//��ӡ��������Ϣ

		//��ȡĳ������������ϸ��Ϣ
		Sensor sensor = api.sensor_get(100009);
		System.out.println(sensor.toString());

		//���һ���µĴ���������Ҫָ�������豸id��������������name������������type
		api.sensors_post(10012, "java-sensor@" + date, 1, "tags", "sensor created by java api, at " + date);

		//ͨ��IDɾ��ָ���Ĵ�����
		api.sensor_delete(100009);

		//����ĳ������������Ϣ
		api.sensor_put(100009, "zhangxing", 1, "test", "test");
*/
/*
		//���ݵ�ӿڲ���
		System.out.println("<--------���ݵ㣨datapoint���ӿڲ���-------->");

		//��ĳ�����������һ�����ݵ�
		api.datapoint_post(100009, "hello world");

		//��ȡĳ�����崫���������µ�����
		Datapoint dp = api.datapoint_get(100009);
		System.out.println(dp.toString());//������ݵ����ϸ��Ϣ

		//��ȡĳ�����ݵ����ʷ����
		Datapoint[] dps = api.datapoints_get(100007, 1000, 0, 0);
		System.out.println(Arrays.toString(dps));//������ݵ���Ϣ
*/
	}

	//HTTP POST����
	//����HTTP�����ע���ԣ��ο�POST����
	private ResponseInfo http_post(String uri, List <BasicNameValuePair> params)
	{
		StringBuilder content = new StringBuilder();//���ڱ�����Ӧ����
		StringBuilder header = new StringBuilder();//���ڱ�����Ӧͷ
		String statusLine = "";//������Ӧͷ�ĵ�һ�У����״̬��
		int statusCode = 404;//��Ӧ״̬��

		//����HTTP CLIENT����
		CloseableHttpClient client = HttpClients.createDefault();

		try {
			String url = protocol + host + uri;//��֯URL
			HttpPost request = new HttpPost(url);//����HTTP�������POST
			request.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));//��ӱ�����
			request.addHeader("Apikey", apikey);//�������ͷ:apikey
			request.addHeader("Accept", "applicaton/json");//�������ͷ:���ý�����������ΪJSON

			CloseableHttpResponse response = client.execute(request);//����HTTP����

			HttpEntity entity = response.getEntity();//��ȡ��Ӧʵ��
			statusLine = response.getStatusLine().toString();//������Ӧ��״̬��
			statusCode = response.getStatusLine().getStatusCode();//������Ӧ��״̬��

			//��֯��Ӧ��ͷ
			HeaderIterator it = response.headerIterator();
			while (it.hasNext()) {
				header.append( it.nextHeader().toString() + "\n");
			}

			//��֯��Ӧ����
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			String line;
			while ((line = reader.readLine()) != null)
			{
				content.append(line+"\n");
			}

			//���ڲ��Ե������䣬�������HTTP��Ӧ���ģ���ע�͵�
			//����ʹ��ResponseInfo#toString()�����
			/*System.out.println("--------HTTP��Ӧ����--------");
			System.out.println(statusLine);
			System.out.println(header.toString());
			System.out.println(content.toString());*/

			reader.close();
			response.close();
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//���ض���ResponseInfo���ö������ڴ洢��Ӧ���ĵ���Ҫ��Ϣ
		return new ResponseInfo(statusCode, statusLine, header.toString(), content.toString());
	}

	private ResponseInfo http_put(String uri, List <BasicNameValuePair> params)
	{
		StringBuilder content = new StringBuilder();
		StringBuilder header = new StringBuilder();
		String statusLine = "";
		int statusCode = 404;
		CloseableHttpClient client = HttpClients.createDefault();

		try {
			String url = protocol + host + uri;
			HttpPut request = new HttpPut(url);
			request.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
			request.addHeader("Apikey", apikey);
			request.addHeader("Accept", "applicaton/json");

			CloseableHttpResponse response = client.execute(request);

			HttpEntity entity = response.getEntity();
			statusLine = response.getStatusLine().toString();
			statusCode = response.getStatusLine().getStatusCode();

			HeaderIterator it = response.headerIterator();
			while (it.hasNext()) {
				header.append( it.nextHeader().toString() + "\n");
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			String line;
			while ((line = reader.readLine()) != null)
			{
				content.append(line+"\n");
			}

			reader.close();
			response.close();
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseInfo(statusCode, statusLine, header.toString(), content.toString());
	}

	private ResponseInfo http_get(String uri)
	{
		StringBuilder content = new StringBuilder();
		StringBuilder header = new StringBuilder();
		String statusLine = "";
		int statusCode = 404;
		CloseableHttpClient client = HttpClients.createDefault();

		try {
			String url = protocol + host + uri;
			HttpGet request = new HttpGet(url);
			request.addHeader("Apikey", apikey);
			request.addHeader("Accept", "applicaton/json");

			CloseableHttpResponse response = client.execute(request);

			HttpEntity entity = response.getEntity();
			statusLine = response.getStatusLine().toString();
			statusCode = response.getStatusLine().getStatusCode();

			HeaderIterator it = response.headerIterator();
			while (it.hasNext()) {
				header.append( it.nextHeader().toString() + "\n");
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			String line;
			while ((line = reader.readLine()) != null)
			{
				content.append(line+"\n");
			}

			reader.close();
			response.close();
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseInfo(statusCode, statusLine, header.toString(), content.toString());
	}

	private ResponseInfo http_delete(String uri)
	{
		StringBuilder content = new StringBuilder();
		StringBuilder header = new StringBuilder();
		String statusLine = "";
		int statusCode = 404;
		CloseableHttpClient client = HttpClients.createDefault();

		try {
			String url = protocol + host + uri;
			HttpDelete request = new HttpDelete(url);
			request.addHeader("Apikey", apikey);
			request.addHeader("Accept", "applicaton/json");

			CloseableHttpResponse response = client.execute(request);

			HttpEntity entity = response.getEntity();
			statusLine = response.getStatusLine().toString();
			statusCode = response.getStatusLine().getStatusCode();

			HeaderIterator it = response.headerIterator();
			while (it.hasNext()) {
				header.append( it.nextHeader().toString() + "\n");
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			String line;
			while ((line = reader.readLine()) != null)
			{
				content.append(line+"\n");
			}

			reader.close();
			response.close();
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseInfo(statusCode, statusLine, header.toString(), content.toString());
	}

	//ͨ���û�����������е�¼
	//��֤�ɹ�������Apikey��ֵ
	//���ر�������Ϣ��USER����
	private User login_post(String username, String password)
	{
		List <BasicNameValuePair> params = new ArrayList <BasicNameValuePair> ();
		params.add(new BasicNameValuePair("username", username));//required
		params.add(new BasicNameValuePair("password", password));//required

		
		ResponseInfo result = this.http_post("/linpcloud/login", params);
		//System.out.println(result.content);//��Ҫ��ʾHTTP������ȡ����ע��

		Gson gson = new Gson();
		if(result.code == 200)//����ɹ�
		{
			//@google gson
			User user = gson.fromJson(result.content, User.class);
			user.code = 200;
			apikey = user.apikey;//����APIKEY
			return user;
		}
		else if(result.code == 400)//����ڴ���
		{
			//@google gson
			User user = gson.fromJson(result.content, User.class);
			user.code = 400;
			//do something to tell me what is wrong
			return user;
		}
		else//Ԥ�������
		{
			//do something to tell me what is wrong
			User user = new User();
			user.code = result.code;
			return user;

			//or return this
			//return null;
		}
	}

	//ͨ���û�ID��ȡ�������û���Ϣ
	//����USER����
	private User user_get(int user_id)
	{
		ResponseInfo result = this.http_get("/linpcloud/user/" + user_id);
		//System.out.println(result.content);

		Gson gson = new Gson();
		if(result.code == 200)
		{
			//@google gson
			User user = gson.fromJson(result.content, User.class);
			user.code = 200;
			return user;
		}
		else if(result.code == 400)
		{
			//@google gson
			User user = gson.fromJson(result.content, User.class);
			user.code = 400;
			//do something to tell me what is wrong
			return user;
		}
		else
		{
			//do something to tell me what is wrong
			User user = new User();
			user.code = result.code;
			return user;

			//or return this
			//return null;
		}
	}

	//��ȡ���û����е��豸
	//���ض�������
	private Device[] devices_get()
	{
		ResponseInfo result = this.http_get("/linpcloud/devices");
		System.out.println(result.content);

		Gson gson = new Gson();
		if(result.code == 200)
		{
			//@google gson
			Device[] devices = gson.fromJson(result.content, new TypeToken<Device[]>(){}.getType());
			return devices;
		}
		else if(result.code == 400)
		{
			//@google gson
			//Device device = gson.fromJson(result.content, Device.class);
			//do something to tell me what is wrong
			//return device;

			return null;
		}
		else
		{
			//do something to tell me what is wrong
			//Device device = new Device();
			//device.code = result.code;
			//return device;

			//or return this
			return null;
		}
	}

	//ͨ��������һ�����豸
	//��ƽ̨����Ϣ���أ�������û�н��д���
	private boolean devices_post(String name, String tags, String about, String locate)
	{
		List <BasicNameValuePair> params = new ArrayList <BasicNameValuePair> ();
		params.add(new BasicNameValuePair("name", name));//required
		params.add(new BasicNameValuePair("tags", tags));//optional
		params.add(new BasicNameValuePair("about", about));//optional
		params.add(new BasicNameValuePair("locate", locate));//optional

		ResponseInfo result = this.http_post("/linpcloud/devices/", params);
		//System.out.println(result.content);

		Gson gson = new Gson();
		if(result.code == 200)
		{
			return true;
		}
		else if(result.code == 400)
		{
			return false;
		}
		else
		{
			return false;
		}
	}

	//ͨ��ID��ȡĳ���豸����ϸ��Ϣ
	//����DEVICE����
	private Device device_get(int device_id)
	{
		ResponseInfo result = this.http_get("/linpcloud/device/" + device_id);
		//System.out.println(result.content);

		Gson gson = new Gson();
		if(result.code == 200)
		{
			//@google gson
			Device device = gson.fromJson(result.content, Device.class);
			device.code = 200;
			return device;
		}
		else if(result.code == 400)
		{
			//@google gson
			Device device = gson.fromJson(result.content, Device.class);
			device.code = 400;
			//do something to tell me what is wrong
			return device;
		}
		else
		{
			//do something to tell me what is wrong
			Device device = new Device();
			device.code = result.code;
			return device;

			//or return this
			//return null;
		}
	}

	//ͨ��IDɾ��ĳ���豸
	//�����ݷ��أ�����ʱû�д���
	private boolean device_delete(int device_id)
	{
		ResponseInfo result = this.http_delete("/linpcloud/device/" + device_id);
		//System.out.println(result.content);

		Gson gson = new Gson();
		if(result.code == 200)
		{
			return true;
		}
		else if(result.code == 400)
		{
			return false;
		}
		else
		{
			return false;
		}
	}

	//����ĳ���豸��Ϣ
	private boolean device_put(int device_id, String name, String tags, String about, String locate)
	{
		List <BasicNameValuePair> params = new ArrayList <BasicNameValuePair> ();
		params.add(new BasicNameValuePair("name", name));//required
		params.add(new BasicNameValuePair("tags", tags));//optional
		params.add(new BasicNameValuePair("about", about));//optional
		params.add(new BasicNameValuePair("locate", locate));//optional

		ResponseInfo result = this.http_put("/linpcloud/device/" + device_id, params);
		//System.out.println(result.content);

		Gson gson = new Gson();
		if(result.code == 200)
		{
			return true;
		}
		else if(result.code == 400)
		{
			return false;
		}
		else
		{
			return false;
		}
	}

	//ͨ���豸ID��ȡ���豸�����еĴ�����
	//���ض�������
	private Sensor[] sensors_get(int device_id)
	{
		ResponseInfo result = this.http_get("/linpcloud/sensors/" + device_id);
		//System.out.println(result.content);

		Gson gson = new Gson();
		if(result.code == 200)
		{
			//@google gson
			Sensor[] sensors = gson.fromJson(result.content, new TypeToken<Sensor[]>(){}.getType());
			return sensors;
		}
		else if(result.code == 400)
		{
			//@google gson
			//Device device = gson.fromJson(result.content, Device.class);
			//do something to tell me what is wrong
			//return device;

			return null;
		}
		else
		{
			//do something to tell me what is wrong
			//Device device = new Device();
			//device.code = result.code;
			//return device;

			//or return this
			return null;
		}
	}

	//�ھ���ĳ���豸�´���һ���µĴ�����
	private boolean sensors_post(int device_id, String name, int type, String tags, String about)
	{
		List <BasicNameValuePair> params = new ArrayList <BasicNameValuePair> ();
		params.add(new BasicNameValuePair("device_id", Integer.toString(device_id)));//required
		params.add(new BasicNameValuePair("type", Integer.toString(type)));//required
		params.add(new BasicNameValuePair("name", name));//required

		params.add(new BasicNameValuePair("tags", tags));//optional
		params.add(new BasicNameValuePair("about", about));//optional

		ResponseInfo result = this.http_post("/linpcloud/sensors/", params);
		//System.out.println(result.content);

		Gson gson = new Gson();
		if(result.code == 200)
		{
			return true;
		}
		else if(result.code == 400)
		{
			return false;
		}
		else
		{
			return false;
		}
	}

	//ͨ��ID��ȡĳ������������ϸ��Ϣ
	private Sensor sensor_get(int sensor_id)
	{
		ResponseInfo result = this.http_get("/linpcloud/sensor/" + sensor_id);
		//System.out.println(result.content);

		Gson gson = new Gson();
		if(result.code == 200)
		{
			//@google gson
			Sensor sensor = gson.fromJson(result.content, Sensor.class);
			sensor.code = 200;
			return sensor;
		}
		else if(result.code == 400)
		{
			//@google gson
			Sensor sensor = gson.fromJson(result.content, Sensor.class);
			sensor.code = 400;
			//do something to tell me what is wrong
			return sensor;
		}
		else
		{
			//do something to tell me what is wrong
			Sensor sensor = new Sensor();
			sensor.code = result.code;
			return sensor;

			//or return this
			//return null;
		}
	}

	//ͨ��IDɾ��ĳ�����崫����
	private boolean sensor_delete(int sensor_id)
	{
		ResponseInfo result = this.http_delete("/linpcloud/sensor/" + sensor_id);
		//System.out.println(result.content);

		Gson gson = new Gson();
		if(result.code == 200)
		{
			return true;
		}
		else if(result.code == 400)
		{
			return false;
		}
		else
		{
			return false;
		}
	}

	//����ĳ���豸����Ϣ
	private boolean sensor_put(int sensor_id, String name, int type, String tags, String about)
	{
		List <BasicNameValuePair> params = new ArrayList <BasicNameValuePair> ();
		params.add(new BasicNameValuePair("name", name));//required
		params.add(new BasicNameValuePair("type", Integer.toString(type)));//required

		params.add(new BasicNameValuePair("tags", tags));//optional
		params.add(new BasicNameValuePair("about", about));//optional

		ResponseInfo result = this.http_put("/linpcloud/sensor/" + sensor_id, params);
		//System.out.println(result.content);

		Gson gson = new Gson();
		if(result.code == 200)
		{
			return true;
		}
		else if(result.code == 400)
		{
			return false;
		}
		else
		{
			return false;
		}
	}

	//ͨ��ID������������һ�����ݵ�
	//Ҳ������Ϊ������������
	private boolean datapoint_post(int sensor_id, String value)
	{
		List <BasicNameValuePair> params = new ArrayList <BasicNameValuePair> ();
		params.add(new BasicNameValuePair("sensor_id", Integer.toString(sensor_id)));//required
		params.add(new BasicNameValuePair("value", value));//required

		ResponseInfo result = this.http_post("/linpcloud/datapoint", params);
		//System.out.println(result.content);

		Gson gson = new Gson();
		if(result.code == 200)
		{
			return true;
		}
		else if(result.code == 400)
		{
			return false;
		}
		else
		{
			return false;
		}
	}

	//��ȡĳ�����������������ݵ�
	private Datapoint datapoint_get(int sensor_id)
	{
		ResponseInfo result = this.http_get("/linpcloud/datapoint/" + sensor_id);
		//System.out.println(result.content);

		Gson gson = new Gson();
		if(result.code == 200)
		{
			//@google gson
			Datapoint datapoint = gson.fromJson(result.content, Datapoint.class);
			datapoint.code = 200;
			return datapoint;
		}
		else if(result.code == 400)
		{
			//@google gson
			Datapoint datapoint = gson.fromJson(result.content, Datapoint.class);
			datapoint.code = 400;
			//do something to tell me what is wrong
			return datapoint;
		}
		else
		{
			//do something to tell me what is wrong
			Datapoint datapoint = new Datapoint();
			datapoint.code = result.code;
			return datapoint;

			//or return this
			//return null;
		}
	}

	//��ȡĳ������������ʷ����
	//start-��ʼ��ʱ���������Ϊ0��Ĭ����ΪһСʱǰ
	//end-������ʱ���������Ϊ0��Ĭ��������
	//interval-ʱ����������Ϊ0��Ĭ��ֵ60��
	//����ֵ��Ϊ�Ǹ�����
	//ǰ����ΪUNIXʱ�������XX��XX��XX����������������Ǻ���
	//����ֵΪ��������
	private Datapoint[] datapoints_get(int sensor_id, int start, int end, int interval)
	{
		String url = "/linpcloud/datapoints/sensor/" + sensor_id;
		if(start != 0)
		{
			url += "?start=" + start;

			if(end != 0)
			{
				url += "&end=" + end;

				if(interval != 0)
				{
					url += "&interval=" + interval;
				}
			}
		}
		System.out.println(url);

		ResponseInfo result = this.http_get(url);
		//System.out.println(result.content);

		Gson gson = new Gson();
		if(result.code == 200)
		{
			//parse the json string to array
			Datapoint[] datapoints = gson.fromJson(result.content, new TypeToken<Datapoint[]>(){}.getType());
			return datapoints;
		}
		else if(result.code == 400)
		{
			//@google gson
			//Device device = gson.fromJson(result.content, Device.class);
			//do something to tell me what is wrong
			//return device;

			return null;
		}
		else
		{
			//do something to tell me what is wrong
			//Device device = new Device();
			//device.code = result.code;
			//return device;

			//or return this
			return null;
		}
	}

}

//���ڴ洢��Ӧ������Ϣ
class ResponseInfo
{
	//http response status code
	//for 200, request is success and platform work properly
	//for 400, request is success but platform encounter an error and return error msg
	//for other code, request is treated to be fail
	protected int code;

	protected String statusLine;
	protected String header;

	//response body, or response content
	//for 200 and 400, this should be a json string contained infomation
	//for any other, this is treated as invalid
	protected String content;

	ResponseInfo(int statusCode, String statusLine, String header, String content)
	{
		this.code = statusCode;
		this.statusLine = statusLine;
		this.header = header;
		this.content = content;
	}

	public void show()
	{
		System.out.println("--------HTTP��Ӧ����--------");
		System.out.println(statusLine);
		System.out.println(header);
		System.out.println(content);
	}
}

//���ڱ����û���Ϣ
class User
{
	//these tow params you may not need,
	//but if you want to deal with response code after json is parsing,
	//it could be helpful.
	//used to store http response code, set it when deal with http response
	protected int code;
	//used to store message in json string, get from json string
	protected String info;

	protected int id;			//important
	protected String username;	//important
	protected String password;
	protected String email;		//important
	protected int regtime;
	protected String apikey;	//important
	protected String about;
	protected int status;

	//this function is used for test, to show all params
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		builder.append("\n--------SHOW PARAMS FOR USER--------\n");
		builder.append("info = " + info + "\n");

		builder.append("id = " + id + "\n");
		builder.append("username = " + username + "\n");
		builder.append("password = " + password + "\n");
		builder.append("email = " + email + "\n");
		builder.append("regtime = " + regtime + "\n");
		builder.append("apikey = " + apikey + "\n");
		builder.append("status = " + status + "\n");
		builder.append("about = " + about + "\n");

		return builder.toString();
	}
}

//���ڱ����豸��Ϣ
class Device
{
	protected int code;
	protected String info;

	protected int id;
	protected String name;
	protected String tags;
	protected String about;
	protected String locate;
	protected int user_id;
	protected int create_time;
	protected int last_active;
	protected int status;

	//this function is used for test, to show all params
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		builder.append("\n--------SHOW PARAMS FOR DEVICE--------" + "\n");
		builder.append("info = " + info + "\n");

		builder.append("id = " + id + "\n");
		builder.append("name = " + name + "\n");
		builder.append("tags = " + tags + "\n");
		builder.append("about = " + about + "\n");
		builder.append("locate = " + locate + "\n");
		builder.append("user_id = " + user_id + "\n");
		builder.append("create_time = " + create_time + "\n");
		builder.append("last_active = " + last_active + "\n");
		builder.append("status = " + status + "\n");

		return builder.toString();
	}
}

//���ڱ��洫������Ϣ
class Sensor
{
	protected int code;
	protected String info;

	private int id;
	private String name;
	private String tags;
	private String about;
	private int type;
	private int device_id;
	private int last_update;
	private String last_data;
	private int status;

	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		builder.append("\n--------SHOW PARAMS FOR SENSOR--------" + "\n");
		builder.append("info = " + info + "\n");

		builder.append("id = " + id + "\n");
		builder.append("name = " + name + "\n");
		builder.append("tags = " + tags + "\n");
		builder.append("about = " + about + "\n");
		builder.append("type = " + type + "\n");
		builder.append("device_id = " + device_id + "\n");
		builder.append("last_update = " + last_update + "\n");
		builder.append("last_data = " + last_data + "\n");
		builder.append("status = " + status + "\n");

		return builder.toString();
	}
}

//�������ݵ���Ϣ
class Datapoint
{
	protected int code;
	protected String info;

	private int id;
	private int sensor_id;
	private int timestamp;
	private String value;

	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		builder.append("\n--------SHOW PARAMS FOR SENSOR--------" + "\n");
		builder.append("info = " + info + "\n");

		builder.append("id = " + id + "\n");
		builder.append("sensor_id = " + sensor_id + "\n");
		builder.append("timestamp = " + timestamp + "\n");
		builder.append("value = " + value + "\n");

		return builder.toString();
	}
}

//��ʱû��
class ErrorInfo
{
	protected String info;
}