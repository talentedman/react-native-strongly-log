
#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface SLog : NSObject

+(void)setup;

+(NSString*)getLogFileFolder;
+(NSString*)getLogZipFolder;
+(NSString*)getLogFile;

+(void)clearOldLogs;


+(void)v:(NSString*)msg;
+(void)v:(NSString*)tag msg:(NSString*)msg;

+(void)d:(NSString*)msg;
+(void)d:(NSString*)tag msg:(NSString*)msg;

+(void)i:(NSString*)msg;
+(void)i:(NSString*)tag msg:(NSString*)msg;

+(void)w:(NSString*)msg;
+(void)w:(NSString*)tag msg:(NSString*)msg;

+(void)e:(NSString*)msg;
+(void)e:(NSString*)tag msg:(NSString*)msg;

@end

NS_ASSUME_NONNULL_END
