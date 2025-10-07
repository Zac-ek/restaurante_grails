package com.ordenaris.restaurante
import com.ordenaris.restaurante.TipoMenu
import grails.gorm.transactions.Transactional

@Transactional
class PlatilloService {

    def listaPlatillos(tipo){
        try{
            def list
            if(tipo){
                def menu = TipoMenu.findById(tipo.toInteger())
                list = Platillo.findAllByStatusNotEqualsAndTipoMenu(2, menu);
                // println(list)
            }else{
                list = Platillo.findAllByStatusNotEquals(2);
            }

            def lista = list.collect{ platillo ->
                def object = [
                    nombre: platillo.nombre,
                    descripcion: platillo.descripcion,
                    tipo_menu: platillo.tipoMenuId,
                    cantidad: platillo.platillosDisponibles,
                    costo: (platillo.costo/100),
                    estado: platillo.status,
                    identificador: platillo.uuid
                ]
                return object
            }

            return [
                res: [ success: true, data: lista],
                status: 200
            ]
        }catch(e){
            return [
                res: [ success: false, mensaje: e.getMessage() ],
                status: 500
            ]
        }
    }

    def nuevoPlatillo( nombre, descripcion, costo, cantidad, fechDis, tipo){
        try{
            def tipo_menu = TipoMenu.findByUuid(tipo)
            if(!tipo_menu){
                return [
                res: [ success: false, message: "El tipo de menu no existe" ],
                status: 400
            ]
            }
            def fecha
            def sta = 1
            if(fechDis){
                fecha = fechDis + 'T00:00:00Z'
                sta = 0
            }
            def nuevoPlato
            if(cantidad){
                nuevoPlato = new Platillo([
                    nombre: nombre, 
                    descripcion: descripcion, 
                    costo: costo, 
                    platillosDisponibles: cantidad, 
                    fechaDisponible: fecha, 
                    tipoMenu: tipo_menu,
                    status: sta
                ]).save(flush:true, failOnError:true)
            }else{
                nuevoPlato = new Platillo([
                    nombre: nombre, 
                    descripcion: descripcion, 
                    costo: costo, 
                    fechaDisponible: fecha, 
                    tipoMenu: tipo_menu,
                    status: sta
                ]).save(flush:true, failOnError:true)
            }
            return [
                res: [ success: true, data: nuevoPlato ],
                status: 200
            ]
        }catch(e){
            return [
                res: [ success: false, mensaje: e.getMessage() ],
                status: 500
            ]
        }
    }

    def informacionPlatillo( uuid ){
        try{
            def platillo = Platillo.findByUuid(uuid)
            def menu = TipoMenu.findById(platillo.tipoMenuId)
            if(!platillo) {
                return [
                    resp: [ success:false, mensaje: "El platillo no existe" ],
                    status: 404
                ]
            }
            if(platillo.status == 2){
                return [
                    resp: [ success:false, mensaje: "El platillo ha sido eliminado" ],
                    status: 404
                ]
            }

            def data = [
                nombre: platillo.nombre,
                descripcion: platillo.descripcion,
                tipo_menu: menu.nombre,
                cantidad: platillo.platillosDisponibles,
                costo: (platillo.costo/100),
                estado: platillo.status,
                identificador: platillo.uuid
            ]
            return [
                res: [ success:true, data: data ],
                status: 200
            ]
        }catch(e){
            return [
                res: [ success: false, mensaje: e.getMessage() ],
                status: 500
            ]
        }
    }

    def editarPlatillo( uuid, nombre, descripcion, costo, cantidad, fechDis ){
        try{
            def platillo = Platillo.findByUuid(uuid)
            println(platillo)

            if(!platillo){
                return [
                    res: [ success: false, mensaje: "El Platillo no existe realmente" ],
                    status: 500
                ]
            }
            if(nombre){
                platillo.nombre = nombre
            }
            if(descripcion){
                platillo.descripcion = descripcion
            }
            if(costo){
                platillo.costo = costo.toInteger()
            }
            if(cantidad){                
                platillo.platillosDisponibles = cantidad.toInteger()
            }
            if(fechDis){                
                platillo.fechaDisponible = fechDis + 'T00:00:00Z'
                platillo.status = 1
            }else{                
                platillo.fechaDisponible = null
                platillo.status = 0
            }
            platillo.save()
            return [
                res: [ success:true, data: platillo ],
                status: 200
            ]
        }catch(e){
            return [
                res: [ success: false, mensaje: e.getMessage() ],
                status: 500
            ]
        }
    }

    def editarEstatusPlatillo( estatus , uuid ){
        try{
            def platillo = Platillo.findByUuid( uuid )
            if( !platillo ) {
                return [
                    res: [success:false, mensaje: "No se encontro el platillo"],
                    status: 500
                ]
            }
            if( estatus == 2 && platillo.status == 2){
                return [
                    res: [success:false, mensaje: "El platillo ya ha sido borrado"],
                    status: 500
                ]
            }
            platillo.status = estatus
            platillo.save()
            return [
                res: [success:true],
                status: 200
            ]
        }catch(e){
            return [
                res: [ success: false, mensaje: e.getMessage() ],
                status: 500
            ]
        }
    }

    def paginarPlatillos( pagina, columna, orden, max, status, query ){
        try{
            def offset = pagina * max - max
            def platillos = Platillo.createCriteria().list{
                if( status ) {
                    eq("status", status)
                }
                if( query ){
                    like("nombre", "%${query}%")
                }
                firstResult(offset)
                maxResults(max)
                order(columna, orden)
            }.collect{ platillo ->
                def object = [
                    nombre: platillo.nombre,
                    descripcion: platillo.descripcion,
                    tipo_menu: platillo.tipoMenuId,
                    cantidad: platillo.platillosDisponibles,
                    costo: (platillo.costo/100),
                    estado: platillo.status,
                    identificador: platillo.uuid
                ]
                return object
            }
            return [
                res: [ success: true, data: platillos ],
                status: 200
            ]
        }catch(e){
            return [
                res: [ success: false, mensaje: e.getMessage() ],
                status: 500
            ]
        }
    }
}
