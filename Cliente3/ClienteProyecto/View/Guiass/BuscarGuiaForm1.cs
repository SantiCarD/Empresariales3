﻿
using ClienteProyecto.Model;
using ClienteProyecto.Service;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace ClienteProyecto.View
{
    public partial class BuscarGuiaForm1 : Form
    {
        private GuiasService _service;
        public BuscarGuiaForm1()
        {
            InitializeComponent();
            _service = new GuiasService();
            LlenarComboCriterio();
        }

        private void LlenarComboCriterio()
        {
            // Limpia el ComboBox para evitar duplicados
            cbCriterio.Items.Clear();

            // Agrega las opciones "Id" y "Nombre" al ComboBox
            cbCriterio.Items.Add("Id");
            cbCriterio.Items.Add("Nombre");

            // Selecciona el primer elemento por defecto
            cbCriterio.SelectedIndex = 0;
        }

        private void btnBuscar_Click(object sender, EventArgs e)
        {
            try
            {
                string criterio = cbCriterio.SelectedItem.ToString();
                Guia guia = null;

                if (criterio == "Id") // Si el criterio es buscar por Id
                {
                    int resultado;
                    if (int.TryParse(txtValor.Text, out resultado))
                    {
                        // Realizamos la búsqueda por Id utilizando el servicio REST
                        guia = _service.BuscarGuiaPorId(resultado);
                        MessageBox.Show(guia.ToString());
                    }
                    else
                    {
                        MessageBox.Show("Por favor, ingrese un Id válido.");
                        return;
                    }
                }
                else if (criterio == "Nombre") // Si el criterio es buscar por Nombre
                {
                    string nombre = txtValor.Text;
                    // Realizamos la búsqueda por Nombre utilizando el servicio REST
                    guia = _service.BuscarGuiaPorNombre(nombre);
                }

                if (guia != null)
                {

                    MostrarGuia(guia);
                }
                else
                {
                    MessageBox.Show("Paquete no encontrado.");
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Error: {ex.Message}");
            }
        }
        private void MostrarGuia(Guia guia)
        {
            txtId.Text = guia.id.ToString();
            txtNombre.Text = guia.nombre;
            txtPrecio.Text = guia.calificacion.ToString();
            textFechaI.Text = guia.edad.ToString();
            textFechaF.Text = guia.fechaNacimiento.ToShortDateString();
        }

        private void txtId_TextChanged(object sender, EventArgs e)
        {

        }

        private void label2_Click(object sender, EventArgs e)
        {

        }
    }
}
