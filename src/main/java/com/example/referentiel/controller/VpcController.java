package com.example.referentiel.controller;

import com.example.referentiel.exception.ResourceNotFoundException;
import com.example.referentiel.model.Account;
import com.example.referentiel.model.Cidr;
import com.example.referentiel.model.Vpc;
import com.example.referentiel.repository.AccountRepository;
import com.example.referentiel.repository.CidrRepository;
import com.example.referentiel.repository.VpcRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
public class VpcController {

    @Autowired
    private VpcRepository vpcRepository;

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private CidrRepository cidrRepository;

    @GetMapping("/accounts/{accountId}/vpcs")
    public List<Vpc> getVpcsByProductId(@PathVariable Long accountId) {
        return vpcRepository.findByAccountId(accountId);
    }
    
    @GetMapping("/vpcs")
    Collection<Vpc> vpcs() {
    	
        return vpcRepository.findAll();
    }
    
    @GetMapping("/vpcs/{id}")
    ResponseEntity<?> getVpc(@PathVariable Long id) {
        Optional<Vpc> vpc = vpcRepository.findById(id);
        
        System.out.println("product:" +  vpc.toString());
        
        return vpc.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
/*
    @PostMapping("/products/{productId}/vpcs")
    public Vpc addVpc(@PathVariable Long productId,
                            @Valid @RequestBody Vpc vpc) {
        return productRepository.findById(productId)
                .map(product -> {
                    vpc.setProduct(product);
                    return vpcRepository.save(vpc);
                }).orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));
    }

*/
    @PostMapping("/accounts/{accountId}/vpcs")
    
    public Vpc addVpc(@PathVariable String accountId,
                            @Valid @RequestBody Vpc vpc) {
    	System.out.println("account:" +  accountId);
    	long prdId = Long.valueOf(accountId);
    	
    	Optional<Cidr> cidr = cidrRepository.findById(vpc.getCidr().getId());
    	
    	System.out.println("1 vpc:" + vpc.getCidr().getId() + "   " + vpc.getCidr().getEnv());
    	System.out.println("2 vpc:" + cidr.get().getId() + "   " + cidr.get().getEnv());
    	

    	//vpc.getCidr().setEnv(cidr.get().getEnv());
    	//vpc.getCidr().setText(cidr.get().getText());
    	
    	
    	
        return accountRepository.findById(prdId)
                .map(account -> {
                	//System.out.println("product:" +  product.getAccount().getNumAccount() + " " + product.getAccount().getId());
                    vpc.setAccount(account);
                    cidr.get().setVpc(vpc);
                    vpc.setCidr(cidr.get());
                    
                    return vpcRepository.save(vpc);
                    
                }).orElseThrow(() -> new ResourceNotFoundException("account not found with id " + accountId));
                
    }
    
    
    
    @PutMapping("/accounts/{accountId}/vpcs/{vpcId}")
    public Vpc updateVpc(@PathVariable Long accountId,
                               @PathVariable Long vpcId,
                               @Valid @RequestBody Vpc vpcRequest) {
        if(!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("account not found with id " + accountId);
        }
        Optional<Account> account = accountRepository.findById(accountId);
        return vpcRepository.findById(vpcId)
                .map(vpc -> {
                    vpc.setText(vpcRequest.getText());
                    vpc.setName(vpcRequest.getName());
                    vpc.setCidr(vpcRequest.getCidr());//cidr
                    vpc.setAccount(account.get());
                    return vpcRepository.save(vpc);
                }).orElseThrow(() -> new ResourceNotFoundException("Vpc not found with id " + vpcId));
    }

    @DeleteMapping("/accounts/{accountId}/vpcs/{vpcId}")
    public ResponseEntity<?> deleteVpc(@PathVariable Long accountId,
                                          @PathVariable Long vpcId) {
        if(!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("account not found with id " + accountId);
        }

        return vpcRepository.findById(vpcId)
                .map(vpc -> {
                    vpcRepository.delete(vpc);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("Vpc not found with id " + vpcId));

    }
}
