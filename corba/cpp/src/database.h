#ifndef DATABASE_H
#define DATABASE_H

#include <algorithm>
#include <string>
#include <map>
#include <vector>

#include <boost/bind.hpp>
#include <boost/variant.hpp>

#include "dbobject.h"

namespace CORBA
{
    class Any;
}

namespace Alitheia
{
    class Database
    {
    public:
        Database();
        virtual ~Database();

        template< class T >
        bool addRecord( T& object )
        {
            CORBA::Any corbaObject = object;
            const bool result = addCorbaRecord( corbaObject );
            object = T::fromCorba( corbaObject );
            return result;
        }

        bool deleteRecord( const DAObject& object );
        
        template< class T >
        bool updateRecord( T& object )
        {
            CORBA::Any corbaObject = object;
            const bool result = updateCorbaRecord( corbaObject );
            object = T::fromCorba( corbaObject );
            return result;
        }
        
        template< class T >
        T findObjectById( int id )
        {
            return T::fromCorba( *findObjectById( T(), id ) );
        }

        class test{};

        typedef std::string property_map_key;
        typedef boost::variant< int,
                                bool,
                                std::string,
                                Developer,
                                Directory,
                                FileGroup,
                                Metric,
                                MetricType,
                                Plugin,
                                PluginConfiguration,
                                ProjectFile,
                                ProjectFileMeasurement,
                                ProjectVersion,
                                ProjectVersionMeasurement,
                                StoredProject > property_map_value;
        typedef std::map< property_map_key, property_map_value > property_map;
        typedef property_map_value db_row_entry;

        template< class T >
        std::vector< T > findObjectsByProperties( const property_map& properties )
        {
            using namespace boost;
            const std::vector< CORBA::Any > objects = findObjectsByProperties( T(), properties );
            std::vector< T > result;
            result.resize( objects.size() );
            std::transform( objects.begin(), 
                            objects.end(), 
                            result.begin(), 
                            bind( &T::fromCorba, _1 ) );
            return result;
        }

        std::vector< db_row_entry > doHQL( const std::string& hql, const property_map& params = property_map() );
        std::vector< db_row_entry > doSQL( const std::string& sql, const property_map& params = property_map() );

    private:
        CORBA::Any* findObjectById( const CORBA::Any& type, int id );
        std::vector< CORBA::Any > findObjectsByProperties( const CORBA::Any& type, const property_map& );
        bool addCorbaRecord( CORBA::Any& record );
        bool updateCorbaRecord( CORBA::Any& record );

        class Private;
        Private* const d;
    };
}

#endif
