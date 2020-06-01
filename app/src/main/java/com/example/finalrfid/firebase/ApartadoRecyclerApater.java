package com.example.finalrfid.firebase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalrfid.R;

import java.util.ArrayList;
import java.util.List;

public class ApartadoRecyclerApater extends RecyclerView.Adapter<ApartadoRecyclerApater.ItemViewHolder> {
    private int resource;
    private ArrayList<Apartado> list = new ArrayList<Apartado>();

    public ApartadoRecyclerApater(ArrayList<Apartado> list, int resource){
        this.list = list;
        this.resource =  resource;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource,parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        Apartado apartado = (Apartado) this.list.get(position);
        holder.nombreTextView.setText(apartado.getNombre());
        holder.matriculaTextView.setText(apartado.getMatricula());
        holder.canchaTextView.setText(apartado.getCancha());
        holder.horaTextView.setText(apartado.getHoras());
        holder.tiempoTextView.setText(apartado.getHorasPagadas());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public View view;
        private TextView nombreTextView;
        private TextView matriculaTextView;
        private TextView canchaTextView;
        private TextView horaTextView;
        private TextView tiempoTextView;



        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = view;
            this.nombreTextView = (TextView) view.findViewById(R.id.nombre_alumno);
            this.matriculaTextView = (TextView) view.findViewById(R.id.matricula_alumno);
            this.canchaTextView = (TextView) view.findViewById(R.id.cancha_elegida);
            this.horaTextView = (TextView) view.findViewById(R.id.hora_renta);
            this.tiempoTextView = (TextView) view.findViewById(R.id.tiempo_renta);
        }
    }
}

