package Tg.OSEOR.DIWA.Backend.utils;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Réponse standard de l'API")
public class BaseResponse<T> {
	
    @Schema(description = "Code de statut HTTP ou personnalisé", example = "200")
	private Integer statut;

    @Schema(description = "Message de description", example = "Opération réussie")
	private String description;

    @Schema(description = "Données de la réponse")
	private T data;
	
	public BaseResponse() {
		super();
	}
	
	public BaseResponse(Integer statut, String description, T data) {
		super();
		this.statut = statut;
		this.description = description;
		this.data = data;
	}
	
	public Integer getStatut() { return statut; }
	public void setStatut(Integer statut) { this.statut = statut; }
	
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	
	public T getData() { return data; }
	public void setData(T data) { this.data = data; }

	// Méthodes statiques utilitaires pour simplifier les contrôleurs
	public static <T> BaseResponse<T> success(T data, String message) {
		return new BaseResponse<>(200, message, data);
	}

	public static <T> BaseResponse<T> error(Integer status, String message) {
		return new BaseResponse<>(status, message, null);
	}
}
