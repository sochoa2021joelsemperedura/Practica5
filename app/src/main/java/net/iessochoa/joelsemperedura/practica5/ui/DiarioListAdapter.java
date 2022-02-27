package net.iessochoa.joelsemperedura.practica5.ui;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import net.iessochoa.joelsemperedura.practica5.R;
import net.iessochoa.joelsemperedura.practica5.model.DiaDiario;
import net.iessochoa.joelsemperedura.practica5.viewmodels.DiarioViewModel;

import java.util.List;

public class DiarioListAdapter extends ListAdapter<DiaDiario, DiarioListAdapter.DiarioViewHolder> {
    //definimos la interface para el control del click
    private onItemClickListener listener;
    private onItemBorrarClickListener listenerBorrar;

    //************INTERFACES************//
    public interface onItemClickListener{
        void onItemClick(DiaDiario diaDiario);
    }
    public interface onItemBorrarClickListener{
        void onItemBorrarClick(DiaDiario diaDiario);
    }


    //nos permite asignar el listener creado en la actividad
    public void setOnClickListener(onItemClickListener listener)
    {
        this.listener=listener;
    }
    public void setOnBorrarClickListener(onItemBorrarClickListener listener){
        this.listenerBorrar=listener;
    }

    @NonNull
    @Override
    public DiarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView =LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dia,parent,false);
        return new DiarioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DiarioViewHolder holder, int position) {
        //Si hay tareas las recuperamos
        if (getItemCount() > 0) {
            final DiaDiario diaDiario = getItem(position);
            holder.tvResumenItem.setText(diaDiario.getResumen());
            holder.tvFechaItem.setText(diaDiario.getFechaFormatoLocal());

            //Asignacion imagen emoticono
            if (diaDiario.getValoracionDia() < 5) {
                holder.ivValoracionItem.setImageResource(R.drawable.ic_sad);
            } else if (diaDiario.getValoracionDia() < 8) {
                holder.ivValoracionItem.setImageResource(R.drawable.ic_neutro);
            } else if (diaDiario.getValoracionDia() >= 8){
                holder.ivValoracionItem.setImageResource(R.drawable.ic_smile);
            }

        }
    }
    public DiaDiario getDiaDiarioAt(int posicion){
        return getItem(posicion);
    }

    public class DiarioViewHolder extends RecyclerView.ViewHolder {
        //Views del item
        private TextView tvResumenItem;
        private TextView tvFechaItem;
        private ImageView ivBorrarItem;
        private ImageView ivValoracionItem;
        private ConstraintLayout clItem;
        private CardView cvItem;



        public DiarioViewHolder(@NonNull View itemView) {
            super(itemView);
            iniciaViews();

            itemView.setOnClickListener( v ->{
                int posicion = getAdapterPosition();
                if (listener!=null && posicion != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(posicion));
                }
            });
            itemView.setOnClickListener( v->{
                int posicion = getAdapterPosition();
                if (listenerBorrar!=null && posicion != RecyclerView.NO_POSITION){
                    listenerBorrar.onItemBorrarClick(getItem(posicion));
                }
            });

        }

        public DiaDiario getDia(){
            return getCurrentList().get(DiarioViewHolder.this.getBindingAdapterPosition());
        }

        private void iniciaViews() {
            //Inicia las views
            this.tvResumenItem = itemView.findViewById(R.id.tvResumenItem);
            this.tvFechaItem = itemView.findViewById(R.id.tvFechaItem);
            this.ivBorrarItem = itemView.findViewById(R.id.ivBorrarItem);
            this.ivValoracionItem = itemView.findViewById(R.id.ivValoracionItem);
            this.clItem = itemView.findViewById(R.id.clItem);
            this.cvItem = itemView.findViewById(R.id.cvItem);
        }
    }
    protected DiarioListAdapter(){
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<DiaDiario> DIFF_CALLBACK = new DiffUtil.ItemCallback<DiaDiario>() {
        @Override
        public boolean areItemsTheSame(@NonNull DiaDiario oldItem, @NonNull DiaDiario newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull DiaDiario oldItem, @NonNull DiaDiario newItem) {
            return oldItem.getResumen().equals(newItem.getResumen()) &&
                    oldItem.getContenido().equals(newItem.getContenido()) &&
                    oldItem.getFecha().equals(newItem.getFecha()) &&
                    oldItem.getValoracionDia() == newItem.getValoracionDia();
        }
    };
}
