package ma. projet.grpc. controllers;

import io.grpc.stub.StreamObserver;
import ma.projet.grpc.services.CompteService;
import ma.projet.grpc.stubs .*;
import net.devh.boot.grpc.server.service. GrpcService;

import java.util.stream. Collectors;

@GrpcService
public class CompteServiceImpl extends
        CompteServiceGrpc.CompteServiceImplBase {
    private final CompteService compteService;

    public CompteServiceImpl(CompteService compteService) {
        this. compteService = compteService;}

        @Override
        public void allComptes (GetAllComptesRequest request,
                StreamObserver<GetAllComptesResponse> responseObserver) {
            var comptes = compteService. findAllComptes().stream()
                    .map(compte -> Compte.newBuilder()
                    .setId(compte.getId())
                    .setSolde(compte.getSolde())
                    .setDateCreation(compte.getDateCreation())
                    .setType(TypeCompte.valueOf (compte.getType()))
                    .build())
                    .collect(Collectors.toList ());

            responseObserver. onNext (GetAllComptesResponse.newBuilder()
                    .addAllComptes(comptes) .build());
            responseObserver. onCompleted();}

    @Override
    public void saveCompte(SaveCompteRequest request,
                           StreamObserver<SaveCompteResponse> responseObserver) {


        var compteReq = request.getCompte ();
        var compte = new ma.projet.grpc.entities.Compte();
        compte.setSolde(compteReq.getSolde());
        compte.setDateCreation(compteReq.getDateCreation());
        compte.setType (compteReq.getType ().name());

        var savedCompte = compteService.saveCompte (compte);

        var grpcCompte = Compte.newBuilder ()
                .setId(savedCompte.getId())
                .setSolde (savedCompte.getSolde ())
                .setDateCreation(savedCompte.getDateCreation())
                .setType (TypeCompte.valueOf (savedCompte.getType()));

        responseObserver. onNext (SaveCompteResponse.newBuilder ()
                .setCompte(grpcCompte).build());
        responseObserver.onCompleted();
}
    @Override
    public void compteByType(GetCompteTypeRequest request, StreamObserver<GetCompteByTypeResponse> responseObserver) {
        var comptes = compteService.findAllByType(request.getType()).stream().map(compte -> Compte.newBuilder()
                        .setId(compte.getId())
                        .setSolde(compte.getSolde())
                        .setDateCreation(compte.getDateCreation())
                        .setType(TypeCompte.valueOf(compte.getType()))
                        .build())
                .collect(Collectors.toList());
        responseObserver.onNext(GetCompteByTypeResponse.newBuilder()
                .addAllComptes(comptes).build());
        responseObserver.onCompleted();

    }
    @Override
    public void deleteCompteById(GetCompteByIdRequest request, StreamObserver<DeleteCompteResponse> responseObserver) {
        String compteId = request.getId();
        compteService.deleteComptebyId(compteId);
        DeleteCompteResponse response = DeleteCompteResponse.newBuilder()
                .setMessage("Compte supprimé avec succès.")
                .setSuccess(true)
                .build();
        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }
    @Override
    public void compteById(GetCompteByIdRequest request, StreamObserver<GetCompteByIdResponse> responseObserver) {
        var compte = compteService.findCompteById(request.getId());
        if (compte != null) {
            var grpcCompte = Compte.newBuilder()
                    .setId(compte.getId())
                    .setSolde(compte.getSolde())
                    .setDateCreation(compte.getDateCreation())
                    .setType(TypeCompte.valueOf(compte.getType()))
                    .build();
            responseObserver.onNext(GetCompteByIdResponse.newBuilder().setCompte(grpcCompte).build());
        } else {
            responseObserver.onError(new Throwable("Compte non trouvé"));
        }
        responseObserver.onCompleted();}



}