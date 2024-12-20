package com.example.Servidor3DAE.services;

import com.example.Servidor3DAE.exceptions.*;
import com.example.Servidor3DAE.models.CulturalPackage;
import com.example.Servidor3DAE.models.Guide;
import com.example.Servidor3DAE.repositories.CulturalPackageRepository;
import com.example.Servidor3DAE.repositories.GuideRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PackageService implements IPackageService {

    @Autowired
    private CulturalPackageRepository culturalPackageRepository;
    @Autowired
    private GuideRepository guideRepository;

    public boolean guideExist(List<Integer> guideIds) {
        System.out.println("Verificando guías con IDs: " + guideIds);

        for (CulturalPackage cu : culturalPackageRepository.findAll()) {
            System.out.println("Revisando paquete: " + cu.getId() + " - " + cu.getNombre());
            System.out.println("Guías del paquete: " + cu.getGuias().stream()
                    .map(Guide::getId)
                    .collect(Collectors.toList()));

            for (Guide g : cu.getGuias()) {
                System.out.println("Comparando guía: " + g.getId());
                if (guideIds.contains(g.getId())) {
                    System.out.println("¡Guía duplicada encontrada!");
                    return true;
                }

            }
        }

        System.out.println("No se encontraron guías duplicadas");
        return false;
    }


    public List<CulturalPackage> getList() {
        return culturalPackageRepository.findAll();
    }



    ///CRUD//////CRUD//////CRUD//////CRUD//////CRUD//////CRUD//////CRUD//////CRUD//////CRUD//////CRUD//////CRUD//////CRUD///
    // Buscar paquete cultural por ID
    public Optional<CulturalPackage> searchPackageById(int id) {

        return culturalPackageRepository.findById(id);
    }


    // Buscar paquete cultural por nombre
    public Optional<CulturalPackage> searchPackageByName(String nombre) {
        return culturalPackageRepository.findByNombre(nombre);
    }





    public CulturalPackage createPackage(CulturalPackage cp) throws DuplicatedIdException, DuplicatedNameException, InvalidDateRangeException {
        // Verificar si ya existe un paquete con el mismo ID
        if (searchPackageById(cp.getId()).isPresent()) {
            throw new DuplicatedIdException("Ya existe un paquete con este ID.");
        }

        // Verificar si ya existe un paquete con el mismo nombre
        if (searchPackageByName(cp.getNombre()).isPresent()) {
            throw new DuplicatedNameException("Ya existe un paquete con este nombre.");
        }

        // El resto de las validaciones se mantienen igual
        if (cp.getNombre() == null || cp.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del paquete no puede estar vacío.");
        }
        if (cp.getId() <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número positivo.");
        }
        if (cp.getPrecio() == null || cp.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que 0.");
        }
        if (cp.getFechaInicio() == null || cp.getFechaFin() == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin no pueden ser nulas.");
        }
        if (cp.getFechaInicio().isAfter(cp.getFechaFin())) {
            throw new InvalidDateRangeException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }
        List<Integer> guiasPaqueteid= new ArrayList<>();
        for (Guide cu : cp.getGuias()) {
            guiasPaqueteid.add(cu.getId());
        }

            if (cp.getGuias().isEmpty()) {
                System.out.println("xd");
                cp.setGuias(new ArrayList<Guide>());
            }


        if (guideExist(guiasPaqueteid))
        {
            throw new DuplicatedIdException("Ya existe un paquete con uno de esos guias.");
        }

        //hacer que no se pueda enlazar un guia inexistente
        CulturalPackage responsepac = culturalPackageRepository.save(cp);
        for (Guide cu : cp.getGuias()) {
            if(cu.getCulturalPackage()==null)
            {
                cu.setCulturalPackage(cp);
                System.out.println("xd");
            }
            else
            {
                System.out.println("xd");
                throw new DuplicatedIdException("El guia "+cu.getId()+" ya tiene un paquete asociado");
            }
        }
        CulturalPackage responsepack = culturalPackageRepository.save(cp);
        return responsepack;
    }



    public CulturalPackage updatePackage(CulturalPackage cp) throws PackageNotFoundException, InvalidDateRangeException {
        // Buscar el paquete existente por ID
        Optional<CulturalPackage> paqueteExistente = searchPackageById(cp.getId());

        // Verificar si el paquete existe
        if (paqueteExistente == null) {
            throw new PackageNotFoundException("No se encontró un paquete con el ID proporcionado.");
        }

        // Validar que el nuevo nombre no esté vacío
        if (cp.getNombre() == null || cp.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nuevo nombre no puede estar vacío.");
        }

        // Validar que el nuevo precio sea mayor que 0
        if (cp.getPrecio() == null || cp.getPrecio() <= 0) {
            throw new IllegalArgumentException("El nuevo precio debe ser mayor que 0.");
        }

        // Validar que las nuevas fechas no sean nulas
        if (cp.getFechaInicio() == null || cp.getFechaFin() == null) {
            throw new IllegalArgumentException("Las nuevas fechas de inicio y fin no pueden ser nulas.");
        }

        // Validar que la nueva fecha de inicio sea anterior a la fecha de fin
        if (cp.getFechaInicio().isAfter(cp.getFechaFin())) {
            throw new InvalidDateRangeException("La nueva fecha de inicio debe ser anterior a la fecha de fin.");
        }

        // Actualizar los atributos del paquete

        CulturalPackage responsepac = culturalPackageRepository.save(cp);
        for (Guide cu : cp.getGuias()) {
            if(cu.getCulturalPackage()==null)
            {
                cu.setCulturalPackage(cp);
                System.out.println("xd");
            }
            else
            {
                System.out.println("xd");
                throw new DuplicatedIdException("El guia "+cu.getId()+" ya tiene un paquete asociado");
            }
        }
        CulturalPackage responsepack = culturalPackageRepository.save(cp);
        return responsepack;// Retorna el paquete actualizado
    }



    public boolean deletePackage(int id) throws PackageNotFoundException {
        // Buscar el paquete existente por ID
        Optional<CulturalPackage> paqueteExistente = searchPackageById(id);

        // Verificar si el paquete existe
        if (paqueteExistente.isEmpty()) {
            throw new PackageNotFoundException("No se encontró un paquete con el ID proporcionado.");
        }
        for(Guide g : paqueteExistente.get().getGuias())
        {
            g.setCulturalPackage(null);

        }

        // Eliminar el paquete de la lista
        culturalPackageRepository.deleteById(id);
        // Intenta eliminar el paquete

        boolean x= false;
        if(culturalPackageRepository.findById(id).isEmpty())
        {
            x=true;
        }
        // Retornar true si se eliminó con éxito, false de lo contrario
        return x;
    }






    ///CRUD//////CRUD//////CRUD//////CRUD//////CRUD//////CRUD//////CRUD//////CRUD//////CRUD//////CRUD//////CRUD//////CRUD///

    public List<CulturalPackage> listPackages(String filter) throws PackageNotFoundException {
        List<CulturalPackage> filtrados = new ArrayList<>();
        if (culturalPackageRepository.findAll().isEmpty()) {
            throw new PackageNotFoundException("No hay paquetes disponibles.");
        }

        if(filter.isEmpty() || filter.isBlank())
        {
            filtrados = culturalPackageRepository.findAll();
        }

        else{
            for (CulturalPackage culturalPackage : culturalPackageRepository.findAll()) {
                if(culturalPackage.getNombre().contains(filter))
                {
                    filtrados.add(culturalPackage);
                }
            }
        }
        return filtrados;
    }

}

