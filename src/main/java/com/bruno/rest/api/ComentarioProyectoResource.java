package com.bruno.rest.api;

import java.text.SimpleDateFormat;

import com.bruno.org.dao.DataException;
import com.bruno.org.model.ComentarioProyectoDTO;
import com.bruno.org.model.ComentarioTareaDTO;
import com.bruno.org.model.Results;
import com.bruno.org.service.ComentarioProyectoService;
import com.bruno.org.service.ServiceException;
import com.bruno.org.service.impl.ComentarioProyectoServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/comentarioproyecto")
//Singleton
public class ComentarioProyectoResource {

	private ComentarioProyectoService comentarioProyectoService = null;

	public ComentarioProyectoResource() {
		try {
			comentarioProyectoService = new ComentarioProyectoServiceImpl();
			System.out.println("Servicio instanciado");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Path("/{id}")
	@GET
	@Produces
	@Operation(summary = "Busqueda por id de ComentarioProyecto", description = "Recupera todos los datos de un ComentarioProyecto por su id", responses = {
			@ApiResponse(responseCode = "200", description = "ComentarioProyecto encontrado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ComentarioProyectoDTO.class))),
			@ApiResponse(responseCode = "404", description = "ComentarioProyecto no encontrado"),
			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
	public Response findComentarioProyectoById(@PathParam("id") Long id) throws NumberFormatException, DataException, ServiceException {
		ComentarioProyectoDTO p = null;
		try {
			p = comentarioProyectoService.findById(id);
		} catch (Throwable e) {

			e.printStackTrace();
		}
		if (p != null) {
			return Response.ok(p).build();
		} else {
			return Response.status(Status.BAD_REQUEST.getStatusCode(), "Producto " + id + " no encontrado").build();
		}

	}

	@Path("/proyecto/{proyectoId}")
	@GET
	@Operation(
			summary = "Busqueda por id de proyecto", 
			description = "Recupera todos los datos de un comentario por su id de Proyecto", 
			responses = {
			@ApiResponse(responseCode = "200", description = "comentario proyecto encontrado", 
					content = @Content(mediaType = MediaType.APPLICATION_JSON, 
					schema = @Schema(implementation = ComentarioTareaDTO.class))),
			@ApiResponse(responseCode = "404", description = "comentario proyecto no encontrado"),
			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
	@Produces
	public Response findComentarioByProyecto(@PathParam("proyectoId") Long proyectoId)
			throws NumberFormatException, DataException, ServiceException {

		Results<ComentarioProyectoDTO> p = null;
		try {
			p = comentarioProyectoService.findByProyecto(proyectoId, 1, 20);
		} catch (Throwable e) {

			e.printStackTrace();
		}
		if (p != null) {
			return Response.ok(p).build();
		} else {
			return Response.status(Status.BAD_REQUEST.getStatusCode(), "Proyecto " + proyectoId + " no encontrado")
					.build();
		}

	}

	@POST
	@Operation(summary = "Crea ComentarioProyecto", description = "Crea  ComentarioProyecto por su id", responses = {
			@ApiResponse(responseCode = "200", description = "ComentarioProyecto encontrado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ComentarioProyectoDTO.class))),
			@ApiResponse(responseCode = "404", description = "ComentarioProyecto no encontrado"),
			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
	@Consumes("application/x-www-form-urlencoded")
	public Response crearComentarioProyecto(MultivaluedMap<String, String> formParams) {
		try {
			ComentarioProyectoDTO comentarioProyecto = new ComentarioProyectoDTO();

			comentarioProyecto.setComentario(formParams.getFirst("comentario"));

			String empleadoIdStr = formParams.getFirst("empleadoId");
			if (empleadoIdStr != null) {
				comentarioProyecto.setEmpleadoId(Long.parseLong(empleadoIdStr));
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			String fechaHoraStr = formParams.getFirst("fechaHora");
			if (fechaHoraStr != null) {
				comentarioProyecto.setFechaHora(sdf.parse(fechaHoraStr));
			}

			String proyectoIdStr = formParams.getFirst("proyectoId");
			if (proyectoIdStr != null) {
				comentarioProyecto.setProyectoId(Long.parseLong(proyectoIdStr));
			}

			comentarioProyectoService.comentar(comentarioProyecto);

			return Response.status(Response.Status.CREATED).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
		}
	}

}
