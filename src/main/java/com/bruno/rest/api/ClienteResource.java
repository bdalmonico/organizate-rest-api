package com.bruno.rest.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.bruno.org.dao.DataException;
import com.bruno.org.model.ClienteCriteria;
import com.bruno.org.model.ClienteDTO;
import com.bruno.org.model.ProyectoDTO;
import com.bruno.org.model.Results;
import com.bruno.org.service.ClienteService;
import com.bruno.org.service.ServiceException;
import com.bruno.org.service.impl.ClienteServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/cliente")
//Singleton
public class ClienteResource {

	private ClienteService clienteService = null;

	public ClienteResource() {
		try {
			clienteService = new ClienteServiceImpl();
			System.out.println("Servicio instanciado");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Path("/{id}")
	@GET
	@Produces
	@Operation(summary = "Busqueda por id de cliente", description = "Recupera todos los datos de un cliente por su id", responses = {
			@ApiResponse(responseCode = "200", description = "cliente encontrado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ClienteDTO.class))),
			@ApiResponse(responseCode = "404", description = "no encontrado"),
			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
	public Response findById(@PathParam("id") Long id) throws NumberFormatException, DataException, ServiceException {
		ClienteDTO p = null;
		try {
			p = clienteService.findById(id);
		} catch (Throwable e) {

			e.printStackTrace();
		}
		if (p != null) {
			return Response.ok(p).build();
		} else {
			return Response.status(Status.BAD_REQUEST.getStatusCode(), "Cliente " + id + " no encontrado").build();
		}

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBy(@QueryParam("nombre") String nombre, @QueryParam("email") String email,
			@QueryParam("estadoId") Long estadoId, @QueryParam("nifcif") String nifCif,
			@QueryParam("clienteNombre") String clienteNombre, @QueryParam("telefone") String telefone) {
		try {
//			// Criteria... 
			ClienteCriteria criteria = new ClienteCriteria();
			criteria.setNombre(nombre);
			criteria.setEmail(email);
			criteria.setEstadoId(estadoId);
			criteria.setNifCif(nifCif);
			criteria.setTelefone(telefone);

			Results<ClienteDTO> resultados = clienteService.findByCriteria(criteria, 1, 30);

			return Response.ok(resultados).build();
		} catch (Exception e) {
			new WebApplicationException();
			return Response.status(Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
		}

	}

	@POST
	@Operation(summary = "Crea cliente", description = "Crea un cliente", responses = {
			@ApiResponse(responseCode = "200", description = "cliente encontrado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ClienteDTO.class))),
			@ApiResponse(responseCode = "404", description = "cliente no encontrado"),
			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
	@Consumes("application/x-www-form-urlencoded")
	public Response create(MultivaluedMap<String, String> formParams) {
		try {
			ClienteDTO cliente = new ClienteDTO();
			cliente.setNombre(formParams.getFirst("nombre"));
			cliente.setEmail(formParams.getFirst("email"));
			cliente.setNifCif(formParams.getFirst("nifCif"));
			cliente.setTelefone(formParams.getFirst("telefone"));

			String estadoIdStr = formParams.getFirst("estadoId");
			if (estadoIdStr != null) {
				cliente.setEstadoId(Long.parseLong(estadoIdStr));
			}
			clienteService.registrar(cliente);
			return Response.status(Response.Status.CREATED).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
		}
	}

}
