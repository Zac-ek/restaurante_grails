package com.ordenaris.restaurante


import grails.rest.*
import grails.converters.*

class PlatilloController {
	static responseFormats = ['json', 'xml']
	def PlatilloService

    def listaPlatillos() {
        def res = PlatilloService.listaPlatillos(params.tipo)
        // println res
        return respond( res.res, status: res.status)
    }

    def nuevoPlatillo() {
        def data = request.JSON
        if( !data.nombre ) {
            return respond([success:false, mensaje: "El nombre es obligatorio"], status: 400)
        }
        if( data.nombre.soloNumeros() ) {
            return respond([success:false, mensaje: "El nombre debe contener letras y no solo numeros"], status: 400)
        }
        if( data.nombre.size() > 80 ) {
            return respond([success:false, mensaje: "El nombre no puede ser tan largo"], status: 400)
        }
        if( !data.descripcion ) {
            return respond([success:false, mensaje: "La descripcion es obligatorio"], status: 400)
        }
        if( data.descripcion.size() > 100 ) {
            return respond([success:false, mensaje: "La descripcion no puede ser tan largo"], status: 400)
        }
        if( !data.costo ) {
            return respond([success:false, mensaje: "El costo es obligatorio"], status: 400)
        }
        if( !data.costo.soloNumeros() ) {
            return respond([success:false, mensaje: "El costo debe contener solo numeros"], status: 400)
        }
        if( data.cantidad ) {
            if( data.cantidad && data.cantidad.toInteger() > 200 && data.cantidad.toInteger() < -1 ) {
                return respond([success:false, mensaje: "La cantidad no es valida"], status: 400)
            }
            if( !data.cantidad.soloNumeros() ) {
                return respond([success:false, mensaje: "la cantidad debe contener solo numeros"], status: 400)
            }
        }
        if( data.tipo && data.tipo.size() != 32 ) {
            return respond([success:false, mensaje: "El tipo es invalido"], status: 400)
        }
        def res = PlatilloService.nuevoPlatillo(data.nombre, data.descripcion, data.costo.toInteger(), data.cantidad, data.fechDis, data.tipo)
        return respond( res.res, status: res.status )
    }

    def informacionPlatillo() {
        if( params.uuid.size() != 32 ) {
            return respond([success:false, mensaje: "El uuid es invalido"], status: 400)
        }
        def res = PlatilloService.informacionPlatillo(params.uuid)

        return respond( res.res, status: res.status)
    }

    def editarPlatillo() {
        def data = request.JSON
        if( params.uuid.size() != 32 ){
            return respond([success:false, mensaje: "El uuid es invalido"], status: 400)
        }
        if( data.nombre ){
            if( data.nombre.soloNumeros() ) {
                return respond([success:false, mensaje: "El nombre debe contener letras y no solo numeros"], status: 400)
            }
            if( data.nombre.size() > 80 ) {
                return respond([success:false, mensaje: "El nombre no puede ser tan largo"], status: 400)
            }
        }
        if( data.descripcion ){
            if( data.descripcion.size() > 100 ) {
                return respond([success:false, mensaje: "La descripcion no puede ser tan largo"], status: 400)
            }
        }
        if( data.costo ){
            if( data.costo.toInteger() > 600 && data.costo.toInteger() < 0 ) {
                return respond([success:false, mensaje: "La cantidad no es valida"], status: 400)
            }
            if( !data.costo.soloNumeros() ) {
                return respond([success:false, mensaje: "El costo debe contener solo numeros"], status: 400)
            }
        }
        if( data.cantidad ){
            if( data.cantidad.toInteger() > 200 && data.cantidad.toInteger() < -2 ) {
                return respond([success:false, mensaje: "La cantidad no es valida"], status: 400)
            }
            if( !data.cantidad.soloNumeros() ) {
                return respond([success:false, mensaje: "El costo debe contener solo numeros"], status: 400)
            }
        }
        
        def res = PlatilloService.editarPlatillo(params.uuid, data.nombre, data.descripcion, data.costo, data.cantidad, data.fechDis)
        return respond( res.res, status: res.status )
    }

    def editarEstatusPlatillo() {
        def res = PlatilloService.editarEstatusPlatillo( params.estatus, params.uuid )
        return respond( res.res, status: res.status )
    }

    def paginarPlatillos() {
        def data = params
        if( !data.pagina ) {
            return respond([success:false, mensaje: "La pagina no puede ir vacio"], status: 400)
        }
        if( !data.pagina.soloNumeros() ) {
            return respond([success:false, mensaje: "La pagina debe contener solo numeros"], status: 400)
        }
        if( !data.columna ) {
            return respond([success:false, mensaje: "La columna no puede ir vacio"], status: 400)
        }
        if( !(data.columna in ["nombre", "costo", "platillosDisponibles", "status", "dateCreated"]) ) {
            return respond([success:false, mensaje: "La columna solo puede ser: nombre, costo, platillosDisponibles, status, dateCreated"], status: 400)
        }
        if( !data.orden ) {
            return respond([success:false, mensaje: "El orden no puede ir vacio"], status: 400)
        }
        if( !(data.orden in ["asc", "desc"]) ) {
            return respond([success:false, mensaje: "El orden solo puede ser: asc, desc"], status: 400)
        }
        if( !data.max ) {
            return respond([success:false, mensaje: "El max no puede ir vacio"], status: 400)
        }
        if( !data.max.soloNumeros() ) {
            return respond([success:false, mensaje: "El max debe contener solo numeros"], status: 400)
        }
        if( !(data.max.toInteger() in [ 2, 3, 5 ]) ) {
            return respond([success:false, mensaje: "El max puede ser solo: 2, 3, 5"], status: 400)
        }
        def res = PlatilloService.paginarPlatillos( data.pagina.toInteger(), data.columna, data.orden, data.max.toInteger(), data.status?.toInteger(), params.query )
        return respond( res.res, status: res.status )
    }
}
