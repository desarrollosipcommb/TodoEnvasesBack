package com.sipcommb.envases.dto;

import java.util.Date;
import java.util.Map;

public class CustomApiResponse {
  private final Date tiempo = new Date();
  private String mensaje;
  private String url;

  public CustomApiResponse() {
  }

  public CustomApiResponse(String mensaje, String url) {
    this.mensaje = mensaje;
    this.url = url.replace("uri=", "");
  }

  public CustomApiResponse(String mensaje) {
    this.mensaje = mensaje;
  }

  public Date getTiempo() {
    return tiempo;
  }

  public String getMensaje() {
    return mensaje;
  }

  public void setMensaje(String mensaje) {
    this.mensaje = mensaje;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
