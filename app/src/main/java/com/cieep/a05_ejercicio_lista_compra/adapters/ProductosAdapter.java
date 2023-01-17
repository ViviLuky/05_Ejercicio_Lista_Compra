package com.cieep.a05_ejercicio_lista_compra.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cieep.a05_ejercicio_lista_compra.R;
import com.cieep.a05_ejercicio_lista_compra.configuraciones.Constantes;
import com.cieep.a05_ejercicio_lista_compra.modelos.Producto;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.List;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ProductoVH> {

    private List<Producto> objects;
    private int fila;
    private Context context;


    public ProductosAdapter(List<Producto> objects, int fila, Context context) {
        this.objects = objects;
        this.fila = fila;
        this.context = context;

    }

    @NonNull
    @Override
    public ProductoVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productoView = LayoutInflater.from(context).inflate(fila, null);
        productoView.setLayoutParams(
                new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
        );
        return new ProductoVH(productoView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoVH holder, int position) {
        Producto producto = objects.get(position);
        holder.lblNombre.setText(producto.getNombre());
        holder.txtCantidad.setText(String.valueOf(producto.getCantidad()));

        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDelete(producto).show();
                // notifyDataSetChanged();
            }
        });

        holder.txtCantidad.addTextChangedListener(new TextWatcher() {

            boolean cero = false;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() > 0 && charSequence.charAt(0) == '0')
                    cero = true;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try{
                    if (cero && editable.toString().length() > 1) {
                        holder.txtCantidad.setText(editable.toString().substring(0, 1));
                        holder.txtCantidad.setSelection(1);
                        cero = false;
                    }
                    int cantidad = Integer.parseInt(editable.toString());
                    producto.setCantidad(cantidad);
                    // notifyItemChanged(holder.getAdapterPosition());
                }
                catch (NumberFormatException numberFormatException) {
                    holder.txtCantidad.setText("0");
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProducto(producto).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }


    private androidx.appcompat.app.AlertDialog updateProducto(Producto producto) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle(R.string.alert_edit_title);
        builder.setCancelable(false);

        View productoViewModel = LayoutInflater.from(context).inflate(R.layout.producto_view_model, null);
        TextView lblTotal = productoViewModel.findViewById(R.id.lblTotalProductoViewModel);
        EditText txtNombre = productoViewModel.findViewById(R.id.txtNombreProductoViewModel);
        EditText txtCantidad = productoViewModel.findViewById(R.id.txtCantidadProductoViewModel);
        EditText txtPrecio = productoViewModel.findViewById(R.id.txtPrecioProductoViewModel);



        builder.setView(productoViewModel);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("EVENTO_TEXTO", "BEFORE "+charSequence);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("EVENTO_TEXTO", "ONCHANGED "+charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("EVENTO_TEXTO", "AFTER "+editable.toString());
                try {
                    int cantidad = Integer.parseInt(txtCantidad.getText().toString());
                    float precio = Float.parseFloat(txtPrecio.getText().toString());

                    float total = cantidad * precio;
                    NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
                    lblTotal.setText(numberFormat.format(total));
                }
                catch (NumberFormatException ignored) {}
            }
        };

        txtCantidad.addTextChangedListener(textWatcher);
        txtPrecio.addTextChangedListener(textWatcher);

        txtCantidad.setText(String.valueOf(producto.getCantidad()));
        txtPrecio.setText(String.valueOf(producto.getImporte()));
        txtNombre.setText(producto.getNombre());

        builder.setNegativeButton(R.string.alert_cancel_button, null);
        builder.setPositiveButton(R.string.alert_edit_ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!txtNombre.getText().toString().isEmpty() && !txtCantidad.getText().toString().isEmpty() &&
                        !txtPrecio.getText().toString().isEmpty()) {

                    producto.setCantidad(Integer.parseInt(txtCantidad.getText().toString()));
                    producto.setImporte(Float.parseFloat(txtPrecio.getText().toString()));
                    producto.setNombre(txtNombre.getText().toString());
                    producto.actualizaTotal();

                    notifyItemChanged(objects.indexOf(producto));
                }
                else {
                    Toast.makeText(context, "Faltan Datos", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return builder.create();
    }


    private AlertDialog confirmDelete(Producto producto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("SEGUROOOOOOOO???????");
        builder.setCancelable(false);
        TextView textView = new TextView(context);
        textView.setText(R.string.alert_aviso);
        textView.setTextColor(Color.RED);
        textView.setTextSize(24);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.leftMargin = 50;
        params.rightMargin = 50;
        textView.setLayoutParams(params);
        textView.setPadding(50,50,50,50);
        builder.setView(textView);
        builder.setNegativeButton("Me arrepiento", null);
        builder.setPositiveButton("Con dos cojones", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int posicion = objects.indexOf(producto);
                objects.remove(producto);
                notifyItemRemoved(posicion);
            }
        });
        return builder.create();
    }
    public class ProductoVH extends RecyclerView.ViewHolder {

        TextView lblNombre;
        EditText txtCantidad;
        ImageButton btnEliminar;

        public ProductoVH(@NonNull View itemView) {
            super(itemView);
            lblNombre = itemView.findViewById(R.id.lblNombreProductoViewHolder);
            txtCantidad = itemView.findViewById(R.id.txtCantidadProductoViewHolder);
            btnEliminar = itemView.findViewById(R.id.btnEliminarProductoViewHolder);
        }
    }
}
