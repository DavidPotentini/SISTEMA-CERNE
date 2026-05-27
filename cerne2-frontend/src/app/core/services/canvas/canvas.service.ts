import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  AmbienteCanvasDTO,
  BusinessModelCanvasDTO,
  ChannelImplementationCanvasDTO,
  CustomerPersonasCanvasDTO,
  LeanCanvasDTO,
  ValuePropositionCanvasDTO,
} from '../../../models/canvas/canvas.model';

@Injectable({ providedIn: 'root' })
export class CanvasService {
  private readonly host = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  private base(incCod: number): string {
    return `${this.host}/incubadas/${incCod}/ambienteCanvas`;
  }

  // Ambiente Canvas
  findAmbientesByIncCod(incCod: number): Observable<AmbienteCanvasDTO[]> {
    return this.http.get<AmbienteCanvasDTO[]>(this.base(incCod));
  }
  findAmbienteById(incCod: number, ambcCod: number): Observable<AmbienteCanvasDTO> {
    return this.http.get<AmbienteCanvasDTO>(`${this.base(incCod)}/${ambcCod}`);
  }
  saveAmbiente(incCod: number, body: AmbienteCanvasDTO): Observable<AmbienteCanvasDTO> {
    return this.http.post<AmbienteCanvasDTO>(this.base(incCod), body);
  }
  updateAmbiente(
    incCod: number,
    ambcCod: number,
    body: AmbienteCanvasDTO,
  ): Observable<AmbienteCanvasDTO> {
    return this.http.put<AmbienteCanvasDTO>(`${this.base(incCod)}/${ambcCod}`, body);
  }
  deleteAmbiente(incCod: number, ambcCod: number): Observable<void> {
    return this.http.delete<void>(`${this.base(incCod)}/${ambcCod}`);
  }

  // Business Model Canvas
  findBMC(incCod: number, ambcCod: number): Observable<BusinessModelCanvasDTO> {
    return this.http.get<BusinessModelCanvasDTO>(
      `${this.base(incCod)}/${ambcCod}/businessModelCanvas`,
    );
  }
  saveBMC(
    incCod: number,
    ambcCod: number,
    body: BusinessModelCanvasDTO,
  ): Observable<BusinessModelCanvasDTO> {
    return this.http.post<BusinessModelCanvasDTO>(
      `${this.base(incCod)}/${ambcCod}/businessModelCanvas`,
      body,
    );
  }
  updateBMC(
    incCod: number,
    ambcCod: number,
    body: BusinessModelCanvasDTO,
  ): Observable<BusinessModelCanvasDTO> {
    return this.http.put<BusinessModelCanvasDTO>(
      `${this.base(incCod)}/${ambcCod}/businessModelCanvas`,
      body,
    );
  }
  deleteBMC(incCod: number, ambcCod: number): Observable<void> {
    return this.http.delete<void>(`${this.base(incCod)}/${ambcCod}/businessModelCanvas`);
  }

  // Lean Canvas
  findLean(incCod: number, ambcCod: number): Observable<LeanCanvasDTO> {
    return this.http.get<LeanCanvasDTO>(`${this.base(incCod)}/${ambcCod}/leanCanvas`);
  }
  saveLean(incCod: number, ambcCod: number, body: LeanCanvasDTO): Observable<LeanCanvasDTO> {
    return this.http.post<LeanCanvasDTO>(`${this.base(incCod)}/${ambcCod}/leanCanvas`, body);
  }
  updateLean(incCod: number, ambcCod: number, body: LeanCanvasDTO): Observable<LeanCanvasDTO> {
    return this.http.put<LeanCanvasDTO>(`${this.base(incCod)}/${ambcCod}/leanCanvas`, body);
  }
  deleteLean(incCod: number, ambcCod: number): Observable<void> {
    return this.http.delete<void>(`${this.base(incCod)}/${ambcCod}/leanCanvas`);
  }

  // Value Proposition Canvas
  findVPC(incCod: number, ambcCod: number): Observable<ValuePropositionCanvasDTO> {
    return this.http.get<ValuePropositionCanvasDTO>(
      `${this.base(incCod)}/${ambcCod}/valuePropositionCanvas`,
    );
  }
  saveVPC(
    incCod: number,
    ambcCod: number,
    body: ValuePropositionCanvasDTO,
  ): Observable<ValuePropositionCanvasDTO> {
    return this.http.post<ValuePropositionCanvasDTO>(
      `${this.base(incCod)}/${ambcCod}/valuePropositionCanvas`,
      body,
    );
  }
  updateVPC(
    incCod: number,
    ambcCod: number,
    body: ValuePropositionCanvasDTO,
  ): Observable<ValuePropositionCanvasDTO> {
    return this.http.put<ValuePropositionCanvasDTO>(
      `${this.base(incCod)}/${ambcCod}/valuePropositionCanvas`,
      body,
    );
  }
  deleteVPC(incCod: number, ambcCod: number): Observable<void> {
    return this.http.delete<void>(`${this.base(incCod)}/${ambcCod}/valuePropositionCanvas`);
  }

  // Customer Personas
  findPersonas(incCod: number, ambcCod: number): Observable<CustomerPersonasCanvasDTO[]> {
    return this.http.get<CustomerPersonasCanvasDTO[]>(
      `${this.base(incCod)}/${ambcCod}/customerPersonasCanvas`,
    );
  }
  findPersonaById(
    incCod: number,
    ambcCod: number,
    personaCod: number,
  ): Observable<CustomerPersonasCanvasDTO> {
    return this.http.get<CustomerPersonasCanvasDTO>(
      `${this.base(incCod)}/${ambcCod}/customerPersonasCanvas/${personaCod}`,
    );
  }
  savePersona(
    incCod: number,
    ambcCod: number,
    body: CustomerPersonasCanvasDTO,
  ): Observable<CustomerPersonasCanvasDTO> {
    return this.http.post<CustomerPersonasCanvasDTO>(
      `${this.base(incCod)}/${ambcCod}/customerPersonasCanvas`,
      body,
    );
  }
  updatePersona(
    incCod: number,
    ambcCod: number,
    personaCod: number,
    body: CustomerPersonasCanvasDTO,
  ): Observable<CustomerPersonasCanvasDTO> {
    return this.http.put<CustomerPersonasCanvasDTO>(
      `${this.base(incCod)}/${ambcCod}/customerPersonaCanvas/${personaCod}`,
      body,
    );
  }
  deletePersona(incCod: number, ambcCod: number, personaCod: number): Observable<void> {
    return this.http.delete<void>(
      `${this.base(incCod)}/${ambcCod}/customerPersonaCanvas/${personaCod}`,
    );
  }

  // Channel Implementation
  findChannels(incCod: number, ambcCod: number): Observable<ChannelImplementationCanvasDTO[]> {
    return this.http.get<ChannelImplementationCanvasDTO[]>(
      `${this.base(incCod)}/${ambcCod}/channelImplementationCanvas`,
    );
  }
  findChannelById(
    incCod: number,
    ambcCod: number,
    segCod: number,
  ): Observable<ChannelImplementationCanvasDTO> {
    return this.http.get<ChannelImplementationCanvasDTO>(
      `${this.base(incCod)}/${ambcCod}/channelImplementationCanvas/${segCod}`,
    );
  }
  saveChannel(
    incCod: number,
    ambcCod: number,
    body: ChannelImplementationCanvasDTO,
  ): Observable<ChannelImplementationCanvasDTO> {
    return this.http.post<ChannelImplementationCanvasDTO>(
      `${this.base(incCod)}/${ambcCod}/channelImplementationCanvas`,
      body,
    );
  }
  updateChannel(
    incCod: number,
    ambcCod: number,
    segCod: number,
    body: ChannelImplementationCanvasDTO,
  ): Observable<ChannelImplementationCanvasDTO> {
    return this.http.put<ChannelImplementationCanvasDTO>(
      `${this.base(incCod)}/${ambcCod}/channelImplementationCanvas/${segCod}`,
      body,
    );
  }
  deleteChannel(incCod: number, ambcCod: number, segCod: number): Observable<void> {
    return this.http.delete<void>(
      `${this.base(incCod)}/${ambcCod}/channelImplementationCanvas/${segCod}`,
    );
  }
}
