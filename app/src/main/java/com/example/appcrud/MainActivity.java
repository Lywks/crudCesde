package com.example.appcrud;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class MainActivity extends AppCompatActivity {
    EditText Codigo, Descripcion, Precio;
    Button Insertar, Buscar, Modificar, Eliminar;
    TextView mensajeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(findViewById(R.id.main));
        if (controller != null) {
            controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE);
        }

        View mainView = findViewById(R.id.main);
        mainView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                if (insets != null) {
                    int systemBarsTop = 0;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            systemBarsTop = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
                        }
                    }
                    int systemBarsBottom = 0;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            systemBarsBottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            v.setPadding(insets.getInsets(WindowInsetsCompat.Type.systemBars()).left, systemBarsTop, insets.getInsets(WindowInsetsCompat.Type.systemBars()).right, systemBarsBottom);
                        }
                    }
                }
                return insets.consumeSystemWindowInsets();
            }
        });
        mensajeTextView = findViewById(R.id.mensajeTextView);
        Codigo = findViewById(R.id.codigo);
        Descripcion = findViewById(R.id.descripcion);
        Precio = findViewById(R.id.precio);
        Insertar = findViewById(R.id.insertar);
        Buscar = findViewById(R.id.leer);
        Modificar = findViewById(R.id.modificar);
        Eliminar = findViewById(R.id.eliminar);

        Insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(MainActivity.this, "tienda", null, 1);
                SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

                String codigo = Codigo.getText().toString();
                String descripcion = Descripcion.getText().toString();
                String precio = Precio.getText().toString();

                if (!codigo.isEmpty() && !descripcion.isEmpty() && !precio.isEmpty()) {
                    ContentValues registro = new ContentValues();
                    registro.put("codigo", codigo);
                    registro.put("descripcion", descripcion);
                    registro.put("precio", precio);

                    Codigo.setText("");
                    Descripcion.setText("");
                    Precio.setText("");
                    Toast.makeText(getApplicationContext(), "INFORMACIÓN GUARDADA", Toast.LENGTH_SHORT).show();

                    BaseDeDatos.insert("articulos", null, registro);
                    BaseDeDatos.close();
                } else {
                    Toast.makeText(getApplicationContext(), "LLENA LOS CAMPOS", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(MainActivity.this, "tienda", null, 1);
                SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

                String codigo = Codigo.getText().toString();

                if (!codigo.isEmpty()) {
                    Cursor fila = BaseDeDatos.rawQuery("SELECT descripcion, precio FROM articulos WHERE codigo =" + codigo, null);

                    if (fila.moveToFirst()) {
                        Descripcion.setText(fila.getString(0));
                        Precio.setText(fila.getString(1));

                        Cursor contador = BaseDeDatos.rawQuery("SELECT COUNT(*) FROM articulos WHERE codigo =" + codigo, null);
                        contador.moveToFirst();
                        int cantidadRegistros = contador.getInt(0);
                        contador.close();

                        // Modificación: Establecer el mensaje en el TextView
                        mensajeTextView.setText("Existe " + cantidadRegistros + " del ID: " + codigo);
                    } else {
                        // Modificación: Establecer el mensaje en el TextView
                        mensajeTextView.setText("No Hay libros disponibles");
                    }

                    BaseDeDatos.close();
                } else {
                    // Modificación: Establecer el mensaje en el TextView
                    mensajeTextView.setText("Debes introducir el código del libro");
                }
            }
        });


        Modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(MainActivity.this, "tienda", null, 1);
                SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

                String codigo = Codigo.getText().toString();
                String descripcion = Descripcion.getText().toString();
                String precio = Precio.getText().toString();

                if (!codigo.isEmpty() && !descripcion.isEmpty() && !precio.isEmpty()) {
                    ContentValues registro = new ContentValues();
                    registro.put("codigo",codigo);
                    registro.put("descripcion", descripcion);
                    registro.put("precio", precio);

                    int validar = BaseDeDatos.update("articulos", registro, "codigo =" + codigo, null);
                    BaseDeDatos.close();

                    if (validar == 1) {
                        Toast.makeText(getApplicationContext(), "Registro modificado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "La modificación no se realizó", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Debes llenar todos los datos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(MainActivity.this, "tienda", null, 1);
                SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();
                String codigo = Codigo.getText().toString();

                if (!codigo.isEmpty()) {
                    int validacion = BaseDeDatos.delete("articulos", "codigo=" + codigo, null);
                    BaseDeDatos.close();

                    Codigo.setText("");
                    Descripcion.setText("");
                    Precio.setText("");

                    if (validacion == 1) {
                        Toast.makeText(getApplicationContext(), "Registro eliminado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "No se pudo eliminar", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Debes introducir el código del libro", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
