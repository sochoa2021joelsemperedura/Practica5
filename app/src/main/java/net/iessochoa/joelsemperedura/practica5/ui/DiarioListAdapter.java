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
import androidx.recyclerview.widget.RecyclerView;

import net.iessochoa.joelsemperedura.practica5.R;
import net.iessochoa.joelsemperedura.practica5.model.DiaDiario;
import net.iessochoa.joelsemperedura.practica5.viewmodels.DiarioViewModel;

import java.util.List;

public class DiarioListAdapter extends RecyclerView.Adapter<DiarioListAdapter.DiarioViewHolder> {
    private final LayoutInflater mInflater;
    //Lista con las tareas a mostrar
    private List<DiaDiario> mDiarios; //
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
        View itemView =mInflater.inflate(R.layout.item_dia,parent,false);
        return new DiarioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DiarioViewHolder holder, int position) {
        //Si hay tareas las recuperamos
        if (mDiarios != null) {
            final DiaDiario diaDiario = mDiarios.get(position);
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

    @Override
    public int getItemCount() {
        if (mDiarios != null)
            return mDiarios.size();
        else return 0;
    }
    void setDiarios(List<DiaDiario> diarios){
        mDiarios=diarios;
        notifyDataSetChanged();
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

            //BORRAR ITEM DIA
            ivBorrarItem.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if (listenerBorrar != null){
                        //si se pulsa al icono borrar, le pasamos la nota. Podemos saber la posición del item en la lista
                        listenerBorrar.onItemBorrarClick(mDiarios.get( DiarioViewHolder.this.getAdapterPosition()));

                    }
                }
            });
            //ABRIR ITEM DIA
            cvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        listener.onItemClick(mDiarios.get(DiarioViewHolder.this.getAdapterPosition()));
                    }
                }
            });


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
    DiarioListAdapter(Context context){
        mInflater=LayoutInflater.from(context);
    }
}
