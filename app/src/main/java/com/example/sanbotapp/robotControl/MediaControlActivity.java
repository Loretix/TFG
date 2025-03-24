package com.example.sanbotapp.robotControl;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sanbotapp.R;
import com.google.gson.Gson;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.FaceRecognizeBean;
import com.qihancloud.opensdk.function.beans.StreamOption;
import com.qihancloud.opensdk.function.unit.MediaManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.interfaces.media.FaceRecognizeListener;
import com.qihancloud.opensdk.function.unit.interfaces.media.MediaStreamListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * className: MediaControlActivity
 * function: 多媒体控制
 * <p/> 人脸识别和抓图功能 需要安装“家庭成员app”
 * create at 2017/5/25 9:25
 *
 * @author gangpeng
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class MediaControlActivity extends TopBaseActivity implements TextureView.SurfaceTextureListener {

    private final static String TAG = MediaControlActivity.class.getSimpleName();

    SurfaceView svMedia;
    TextureView tvMedia;
    Button tvCapture, btn_cancel, btn_aceptar;

    LinearLayout llButtons;
    ImageView ivCapture;
    TextView tvFace;

    private SurfaceTexture surfaceTexture;
    private Surface surface;
    private ImageView ivScreenshot;

    private MediaManager mediaManager;
    private SpeechManager speechManager;

    private Uri imageUri;
    private String nombreActividad ="";
    private long mRowId;


    /**
     * 视频编解码器
     */
    MediaCodec mediaCodec;
    /**
     * 视频编解码器超时时间
     */
    long decodeTimeout = 16000;
    MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
    ByteBuffer[] videoInputBuffers;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        onMainServiceConnected();
        setContentView(R.layout.activity_media_control);
        //初始化变量
        mediaManager = (MediaManager) getUnitManager(FuncConstant.MEDIA_MANAGER);
        //svMedia = findViewById(R.id.sv_media);
        tvMedia = findViewById(R.id.tv_media);
        tvCapture = findViewById(R.id.tv_capture);
        ivScreenshot = findViewById(R.id.iv_screenshot);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_aceptar = findViewById(R.id.btn_aceptar);
        llButtons = findViewById(R.id.ll_buttons);

        // Añadimos el speechManager
        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);

        checkPermissions();

        //svMedia.getHolder().addCallback(this);
        // Set TextureView listener
        tvMedia.setSurfaceTextureListener(this);
        initListener();

        // Obtener la URI de la imagen desde el intent
        Intent intent = getIntent();
        if (intent != null) {
            nombreActividad = intent.getStringExtra("nombre_actividad");
            mRowId = intent.getLongExtra("mRowId", -1);
            System.out.println("mRowId inicio: " + mRowId);
            System.out.println("nombreActividad: " + nombreActividad);
        }

        // Capturar imagen
        tvCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onViewClicked();
                // Tomar captura de pantalla
                Bitmap screenshot = takeScreenshotFromTextureView(tvMedia);

                // TODO: PASAR FRAMES DEL VIDEO A LA API
                String base64Image = encodeImageToBase64Bitmap(screenshot);

                /*String task = "expression";
                String method = "VGG19";*/

                String task = "age_gender";
                String method = "MiVOLO";

                if (base64Image != null) {
                    // Enviar la imagen al servidor
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //sendFrameToServer(base64Image);
                            sendImageToServerModels(base64Image, task, method);
                            //SocketManager.sendFrameToServer(base64Image);
                        }
                    }).start();

                    Toast.makeText(MediaControlActivity.this, "Captura mostrada y guardada", Toast.LENGTH_SHORT).show();
                }


                // PASAR IMAGEN
               /* if (screenshot != null) {
                    // Mostrar la captura en el ImageView
                    ivScreenshot.setImageBitmap(screenshot);
                    ivScreenshot.setVisibility(View.VISIBLE);
                    tvCapture.setVisibility(View.GONE);
                    llButtons.setVisibility(View.VISIBLE);

                    // Guardar captura en archivo
                    saveScreenshot(MediaControlActivity.this, screenshot);

                    // Mostrar mensaje
                    Toast.makeText(MediaControlActivity.this, "Captura mostrada y guardada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MediaControlActivity.this, "Error al capturar la imagen", Toast.LENGTH_SHORT).show();
                }*/

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivScreenshot.setVisibility(View.GONE);
                tvCapture.setVisibility(View.VISIBLE);
                llButtons.setVisibility(View.GONE);

            }
        });

        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    String base64Image = encodeImageToBase64(imageUri);

                    if (base64Image != null) {
                        // Enviar la imagen al servidor
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                sendImageToServer(base64Image);

                            }
                        }).start();

                        Toast.makeText(MediaControlActivity.this, "Captura mostrada y guardada", Toast.LENGTH_SHORT).show();
                    }

            }
        });

    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    public String encodeImageToBase64(Uri imageUri) {
        try {

            // Redimensiona la imagen antes de codificarla
            Bitmap resizedBitmap = resizeImage(imageUri, 800, 600); // Ejemplo: máximo 800x600 píxeles

            if (resizedBitmap == null) {
                System.out.println("Error al redimensionar la imagen.");
                return null;
            }

            // Convierte el Bitmap a un array de bytes
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream); // Compresión a calidad 80%
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Codifica los bytes de la imagen en Base64
            String base64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP); // Evita saltos de línea

            System.out.println("Base64: " + base64String);

            base64String = base64String.trim(); // Elimina espacios en blanco alrededor
            base64String = base64String.replace("\n", "").replace("\r", ""); // Elimina saltos de línea

            // Validar que la longitud sea múltiplo de 4
            int remainder = base64String.length() % 4;
            if (remainder != 0) {
                int padding = 4 - remainder; // Calcula el relleno necesario
                for (int i = 0; i < padding; i++) {
                    base64String += "="; // Añade '=' al final de la cadena
                }
            }

            System.out.println("Base64 ESTAAA: " + base64String);

            return base64String;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String encodeImageToBase64Bitmap(Bitmap resizedBitmap) {
        try {

            // Redimensiona la imagen antes de codificarla
            //TODO: PROBAR A HACER UN RESIZE
            //Bitmap resizedBitmap = resizeImage(imageUri, 800, 600); // Ejemplo: máximo 800x600 píxeles

            if (resizedBitmap == null) {
                System.out.println("Error al redimensionar la imagen.");
                return null;
            }

            // Convierte el Bitmap a un array de bytes
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream); // Compresión a calidad 80%
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Codifica los bytes de la imagen en Base64
            String base64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP); // Evita saltos de línea

            System.out.println("Base64: " + base64String);

            base64String = base64String.trim(); // Elimina espacios en blanco alrededor
            base64String = base64String.replace("\n", "").replace("\r", ""); // Elimina saltos de línea

            // Validar que la longitud sea múltiplo de 4
            int remainder = base64String.length() % 4;
            if (remainder != 0) {
                int padding = 4 - remainder; // Calcula el relleno necesario
                for (int i = 0; i < padding; i++) {
                    base64String += "="; // Añade '=' al final de la cadena
                }
            }

            System.out.println("Base64 ESTAAA: " + base64String);

            return base64String;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    public String encodeImageToBase64A(Uri imageUri) {
        try {
            ContentResolver contentResolver = getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(imageUri); // Abre la URI como InputStream

            System.out.println("URI: " + imageUri);
            if (inputStream == null) {
                System.out.println("No se pudo abrir el InputStream de la URI.");
                return null;
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            inputStream.close();

            // Codifica los bytes de la imagen en Base64
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            String base64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP); // Evita saltos de línea

            System.out.println("base64: " + base64String);

            // Asegúrate de que no existan caracteres no válidos en la cadena Base64
            base64String = base64String.trim(); // Elimina espacios en blanco alrededor
            base64String = base64String.replace("\n", "").replace("\r", ""); // Elimina saltos de línea

            // Validar que la longitud sea múltiplo de 4
            int remainder = base64String.length() % 4;
            if (remainder != 0) {
                int padding = 4 - remainder; // Calcula el relleno necesario
                for (int i = 0; i < padding; i++) {
                    base64String += "="; // Añade '=' al final de la cadena
                }
            }

            System.out.println("segundo base64: " + base64String);
            return base64String;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    public void sendImageToServer(String base64Image) {
        try {

            // URL de destino
            URL url = new URL("http://155.210.155.206:8080/sendImage?task=expression&method=VGG19&mode=text");

            // Abrir la conexión HTTP
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);

            // Crear el cuerpo de la solicitud
            DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
            os.write(base64Image.getBytes()); // Escribir los bytes decodificados
            os.flush();
            os.close();

            System.out.println("dataoutputstring: " + os);
            System.out.println("url: " + url);



            // Leer la respuesta
            int code = urlConnection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println("Respuesta del servidor: " + response);


            } else {
                // Manejar errores
                Log.e("MyTag", "Error: " + urlConnection.getResponseMessage());
            }

            urlConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendImageToServerModels(String base64Image, String task, String method) {
        try {

            // URL de destino
            URL url = new URL("http://155.210.155.206:8080/sendImage?task="+task+"&method="+method+"&mode=text");

            // Abrir la conexión HTTP
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);

            // Crear el cuerpo de la solicitud
            DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
            os.write(base64Image.getBytes()); // Escribir los bytes decodificados
            os.flush();
            os.close();

            System.out.println("dataoutputstring: " + os);
            System.out.println("url: " + url);



            // Leer la respuesta
            int code = urlConnection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println("Respuesta del servidor: " + response);

                procesarRespuesta(response.toString(), task);

            } else {
                // Manejar errores
                Log.e("MyTag", "Error: " + urlConnection.getResponseMessage());
            }

            urlConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void procesarRespuesta(String respuesta, String task) {
        // TODO: CAMBIAR EMOCIONES
        if (Objects.equals(task, "expression")) {
            if (respuesta.equals("happy") || respuesta.equals("happiness")) {
                speechManager.startSpeak("Hola, pareces feliz");
            } else if (respuesta.equals("sad") || respuesta.equals("sadness")) {
                speechManager.startSpeak("Hoy parecer un poco triste ¿necesitas ayuda en algo?");
            } else if (respuesta.equals("angry") || respuesta.equals("anger")) {
                speechManager.startSpeak("Eh! No te enfades conmigo, yo no tengo la culpa");
            } else if (respuesta.equals("fear") ) {
                speechManager.startSpeak("¿Tienes miedo de mi?");
            } else if (respuesta.equals("disgust")) {
                speechManager.startSpeak("Parece discustado ¿ocurre algo?");
            } else if (respuesta.equals("surprise")) {
                speechManager.startSpeak("¿Te sorprendi con mis habilidades?");
            } else if (respuesta.equals("neutral")) {
                speechManager.startSpeak("Hola, ¿Cómo estas?");
            }
        } else if (Objects.equals(task, "age_gender")) {
            // La respuesta contiene la edad y el género (por ejemplo, (22, 'female')
            String[] partes = respuesta.split(",");
            String edad = partes[0].trim();
            String genero = partes[1].trim();

            if (genero.equals("'female')")) {
                genero = "mujer";
            } else if (genero.equals("'male')")) {
                genero = "hombre";
            }

            System.out.println("Edad: " + edad + ", Género: " + genero);

            speechManager.startSpeak("Tu edad es " + edad + " y tu genero es " + genero);
        }
    }

    public void sendFrameToServer(String base64Image) {
        try {
            // Configurar la conexión al servidor de Socket.IO
            Socket socket = IO.socket("http://155.210.155.206:8080/videoStream");

            // Manejar la conexión exitosa
            socket.on(Socket.EVENT_CONNECT, args -> {
                System.out.println("Conexión establecida con el servidor");

                try {
                    // Crear el JSON con los datos necesarios
                    JSONObject data = new JSONObject();
                    data.put("task", "expression");
                    data.put("method", "VGG19");
                    data.put("mode", "text");
                    data.put("image", base64Image); // Agregar la imagen en base64

                    System.out.println("JSON enviado: " + data.toString());

                    // Emitir el evento 'sendFrame' con los datos
                    socket.emit("sendFrame", data);
                    System.out.println("Frame enviado al servidor");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // Manejar la respuesta en modo 'text'


            socket.on("sendText", args -> {
                if (args.length > 0) {
                    String recognitionResult = args[0].toString();
                    System.out.println("📩 Resultado recibido (sendText): " + recognitionResult);
                } else {
                    System.out.println("⚠️ Evento 'sendText' recibido, pero sin datos.");
                }
            });

            socket.on("*", args -> {
                System.out.println("📡 Evento recibido desconocido: " + args.toString());
            });

            // Manejar errores de conexión

            socket.on(Socket.EVENT_CONNECT, args -> {
                System.out.println("✅ Conectado al servidor correctamente.");
            });
            socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
                System.err.println("❌ Error de conexión: " + args[0]);
            });
            socket.on(Socket.EVENT_DISCONNECT, args -> {
                System.out.println("🔴 Desconectado del servidor.");
            });


            // Conectar al servidor
            socket.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Bitmap resizeImage(Uri imageUri, int maxWidth, int maxHeight) {
        try {
            // Obtén el ContentResolver y abre el InputStream de la imagen
            ContentResolver contentResolver = getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(imageUri);

            // Decodifica la imagen en un Bitmap para obtener su tamaño original
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // Sólo obtiene las dimensiones de la imagen, no la carga completamente
            BitmapFactory.decodeStream(inputStream, null, options);

            // Calcula el factor de escala para redimensionar la imagen
            int width = options.outWidth;
            int height = options.outHeight;
            int scaleFactor = 1;

            // Si la imagen es más grande que las dimensiones máximas, calcula el factor de escala
            if (width > maxWidth || height > maxHeight) {
                int widthRatio = Math.round((float) width / (float) maxWidth);
                int heightRatio = Math.round((float) height / (float) maxHeight);
                scaleFactor = Math.max(widthRatio, heightRatio);
            }

            // Ahora decodifica la imagen real con el factor de escala
            options.inJustDecodeBounds = false;
            options.inSampleSize = scaleFactor; // Utiliza el factor de escala
            inputStream.close(); // Cierra el inputStream anterior

            inputStream = contentResolver.openInputStream(imageUri); // Vuelve a abrir el InputStream
            Bitmap resizedBitmap = BitmapFactory.decodeStream(inputStream, null, options);

            // Cierra el inputStream
            inputStream.close();

            return resizedBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public Bitmap takeScreenshotFromTextureView(TextureView textureView) {
        if (textureView.isAvailable()) {
            return textureView.getBitmap();
        }
        return null;
    }

    public Bitmap takeScreenshotFromSurfaceView(SurfaceView surfaceView) {
        // Create a Bitmap based on the size of the SurfaceView
        Bitmap bitmap = Bitmap.createBitmap(surfaceView.getWidth(), surfaceView.getHeight(), Bitmap.Config.ARGB_8888);

        // Get the Canvas from the SurfaceView's holder
        Canvas canvas = surfaceView.getHolder().lockCanvas();

        // Check if the Canvas is valid
        if (canvas != null) {
            try {
                // Copy the content of the Canvas to the Bitmap
                bitmap = Bitmap.createBitmap(surfaceView.getWidth(), surfaceView.getHeight(), Bitmap.Config.ARGB_8888);
                surfaceView.getHolder().getSurface().unlockCanvasAndPost(canvas);

                // Create a new Canvas to draw the content into the Bitmap
                Canvas bitmapCanvas = new Canvas(bitmap);
                surfaceView.getHolder().getSurface().lockCanvas(null);

                // Finally, draw the Canvas content to the Bitmap
                bitmapCanvas.drawBitmap(bitmap, 0, 0, null);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }




    public void saveScreenshot(Context context, Bitmap bitmap) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "screenshot_" + System.currentTimeMillis() + ".png");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        imageUri = uri;

        try {
            if (uri != null) {
                OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    /**
     * 初始化监听器
     */
    private void initListener() {
        mediaManager.setMediaListener(new MediaStreamListener() {
            @Override
            public void getVideoStream(byte[] bytes) {
                showViewData(ByteBuffer.wrap(bytes));
            }

            @Override
            public void getAudioStream(byte[] bytes) {
            }
        });
        mediaManager.setMediaListener(new FaceRecognizeListener() {
            @Override
            public void recognizeResult(List<FaceRecognizeBean> list) {
                StringBuilder sb = new StringBuilder();
                for (FaceRecognizeBean bean : list) {
                    sb.append(new Gson().toJson(bean));
                    sb.append("\n");


                    // Acceder al valor de la propiedad "user"
                    String user = bean.getUser();
                    // Hacer algo con el valor de "user"
                    System.out.println("Usuario reconocido: " + user);

                    if(user != ""){
                        speechManager.startSpeak("hola " + user + " ¿cómo estás?");
                    }

                }
                tvFace.setText(sb.toString());
                System.out.println("Persona reconocida????：" + sb.toString());


            }
        });
    }

    /**
     * 显示视频流
     *
     * @param sampleData
     */
    private void showViewData(ByteBuffer sampleData) {
        try {
            int inIndex = mediaCodec.dequeueInputBuffer(decodeTimeout);
            if (inIndex >= 0) {
                ByteBuffer buffer = videoInputBuffers[inIndex];
                int sampleSize = sampleData.limit();
                buffer.clear();
                buffer.put(sampleData);
                buffer.flip();
                mediaCodec.queueInputBuffer(inIndex, 0, sampleSize, 0, 0);
            }
            int outputBufferId = mediaCodec.dequeueOutputBuffer(videoBufferInfo, decodeTimeout);
            if (outputBufferId >= 0) {
                mediaCodec.releaseOutputBuffer(outputBufferId, true);
            } else {
                Log.e(TAG, "dequeueOutputBuffer() error");
            }

        } catch (Exception e) {
            Log.e(TAG, "发生错误", e);
        }
    }

    // TextureView.SurfaceTextureListener methods
    // TextureView.SurfaceTextureListener methods
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        this.surfaceTexture = surfaceTexture;
        this.surface = new Surface(surfaceTexture);

        // Configure stream options and open media stream
        StreamOption streamOption = new StreamOption();
        streamOption.setChannel(StreamOption.MAIN_STREAM);
        streamOption.setDecodType(StreamOption.HARDWARE_DECODE);
        streamOption.setJustIframe(false);
        mediaManager.openStream(streamOption);

        // Configure MediaCodec
        startDecoding(this.surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        // Handle size changes if necessary
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        // Close media stream and stop decoding
        mediaManager.closeStream();
        stopDecoding();
        if (surface != null) {
            surface.release();
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        // Called when the content of the TextureView is updated
    }


    @Override
    protected void onMainServiceConnected() {

    }


    /**
     * 初始化视频编解码器
     *
     * @param surface
     */
    private void startDecoding(Surface surface) {
        if (mediaCodec != null) {
            return;
        }
        try {
            mediaCodec = MediaCodec.createDecoderByType("video/avc");
            MediaFormat format = MediaFormat.createVideoFormat(
                    "video/avc", 1280, 720);
            mediaCodec.configure(format, surface, null, 0);
            mediaCodec.start();
            videoInputBuffers = mediaCodec.getInputBuffers();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 结束视频编解码器
     */
    private void stopDecoding() {
        if (mediaCodec != null) {
            mediaCodec.stop();
            mediaCodec.release();
            mediaCodec = null;
            Log.i(TAG, "stopDecoding");
        }
        videoInputBuffers = null;
    }


    // FUNCION QUE SE REALIZA EN EL ONCLICK DE CAPTURA, LO QUE SE VA A INTENTAR ES O QUE HAGA UNA FOTO O GRABE UN VÍDEO
    public void onViewClicked() {
        //storeImage(mediaManager.getVideoImage());
        //ivCapture.setImageBitmap(mediaManager.getVideoImage());
        saveScreenshot(MediaControlActivity.this, mediaManager.getVideoImage());
    }

    public void storeImage(Bitmap bitmap){
        String dir = Environment.getExternalStorageDirectory()+ "/FACE_REG/IMG/" + "DCIM/";

        System.out.println("RUTA: " + dir);
        final File f = new File(dir);
        if (!f.exists()) {
            f.mkdirs();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(f, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            System.out.println("Imagen guardada en: " + file.getAbsolutePath());
            fos.flush();
            fos.close();
            System.out.println("Imagen guardada en: " + file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
